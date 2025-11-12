import React, {useState} from 'react';
import api, { setAuthToken } from '../api';

export default function Login(){
  const [login,setLogin]=useState('');
  const [senha,setSenha]=useState('');
  const handle=(e)=>{ e.preventDefault(); api.post('/auth/login',{login,senha}).then(r=>{ const t=r.data.token; localStorage.setItem('token',t); setAuthToken(t); window.location.href='/'; }).catch(()=>alert('Erro no login')) }
  return (
    <div style={{maxWidth:400, margin:'40px auto'}}>
      <h2>Login</h2>
      <form onSubmit={handle}>
        <input placeholder="login" value={login} onChange={e=>setLogin(e.target.value)} /><br/>
        <input placeholder="senha" type="password" value={senha} onChange={e=>setSenha(e.target.value)} /><br/>
        <button type="submit">Entrar</button>
      </form>
    </div>
  )
}
