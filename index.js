require('dotenv').config();
const express = require('express');
const mysql = require('mysql2/promise');
const cors = require('cors');
const bodyParser = require('body-parser');
const redis = require('redis');

const app = express();
app.use(cors());
app.use(bodyParser.json());

const dbConfig = {
  host: process.env.DB_HOST || 'localhost',
  user: process.env.DB_USER || 'app_user',
  password: process.env.DB_PASS || '123',
  database: process.env.DB_NAME || 'gestao_matricula',
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0
};

// Add ssl option if DB_SSL=true (useful for PlanetScale)
if (process.env.DB_SSL === '1' || process.env.DB_SSL === 'true') {
  dbConfig.ssl = { rejectUnauthorized: true };
}

let pool;
async function initDb(){
  pool = mysql.createPool(dbConfig);
  const conn = await pool.getConnection();
  await conn.ping();
  conn.release();
  console.log('MySQL conectado ->', dbConfig.host);
}

let redisClient;
async function initRedis(){
  try {
    const rHost = process.env.REDIS_HOST || '127.0.0.1';
    const rPort = process.env.REDIS_PORT ? Number(process.env.REDIS_PORT) : 6379;
    redisClient = redis.createClient({ url: `redis://${rHost}:${rPort}` });
    redisClient.on('error', (err)=> console.error('Redis error', err));
    await redisClient.connect();
    console.log('Redis conectado ->', rHost+':'+rPort);
  } catch(err){
    console.warn('Não foi possível conectar ao Redis (continuando sem fila):', err.message);
    redisClient = null;
  }
}

function gerarId(prefix='USR'){
  const s = Math.random().toString(36).substring(2,10).toUpperCase();
  return `${prefix}-${s}`;
}

app.get('/', (req,res) => res.json({ok:true, service: 'gestao_matricula API'}));

app.post('/usuarios', async (req,res)=>{
  try{
    const { nome, email, senha, grupo } = req.body;
    if(!nome || !email) return res.status(400).json({error: 'nome e email obrigatórios'});
    const id = gerarId('USR');
    const sql = 'INSERT INTO usuarios (id, nome, email, senha, grupo) VALUES (?, ?, ?, ?, ?)';
    await pool.query(sql, [id, nome, email, senha || '', grupo || null]);
    if(redisClient){
      await redisClient.lPush('fila_registro', JSON.stringify({id, email, nome, created_at: new Date().toISOString()}));
    }
    return res.json({status:'OK', id});
  }catch(err){
    console.error(err);
    return res.status(500).json({error: 'erro interno', detail: err.message});
  }
});

app.get('/usuarios', async (req,res)=>{
  try{
    const [rows] = await pool.query('SELECT id, nome, email, grupo, created_at FROM usuarios ORDER BY nome');
    return res.json(rows);
  }catch(err){
    console.error(err);
    return res.status(500).json({error:'erro interno', detail: err.message});
  }
});

app.get('/fila/next', async (req,res)=>{
  try{
    if(!redisClient) return res.status(503).json({error:'redis não disponível'});
    const item = await redisClient.rPop('fila_registro');
    if(!item) return res.json(null);
    return res.json(JSON.parse(item));
  }catch(err){
    console.error(err);
    return res.status(500).json({error:'erro interno', detail: err.message});
  }
});

app.get('/health', (req,res)=> res.json({ok:true, time: new Date().toISOString()}));

const PORT = process.env.PORT || 3000;
(async ()=>{
  try{
    await initDb();
    await initRedis();
    app.listen(PORT, ()=> console.log('API rodando na porta', PORT));
  }catch(err){
    console.error('Erro ao iniciar serviços', err);
    process.exit(1);
  }
})();
