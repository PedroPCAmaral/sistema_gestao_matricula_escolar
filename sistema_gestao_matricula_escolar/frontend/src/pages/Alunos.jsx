import React, {useEffect, useState} from 'react';
import api from '../api';

export default function Alunos(){
  const [alunos,setAlunos]=useState([]);
  useEffect(()=>{ api.get('/alunos').then(r=>setAlunos(r.data)).catch(()=>{}) },[]);
  return (
    <div>
      <h2>Alunos</h2>
      <div style={{display:'grid',gridTemplateColumns:'repeat(3,1fr)',gap:12}}>
        {alunos.map(a=>(
          <div key={a.idAluno} style={{padding:12,boxShadow:'0 1px 4px rgba(0,0,0,.12)'}}>
            <h3>{a.nome}</h3>
            <p>{a.email}</p>
          </div>
        ))}
      </div>
    </div>
  )
}
