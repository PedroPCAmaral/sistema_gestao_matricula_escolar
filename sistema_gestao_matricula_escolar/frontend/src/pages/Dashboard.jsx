import React from 'react';
export default function Dashboard(){
  return (
    <div>
      <h1>Dashboard</h1>
      <section style={{display:'grid',gridTemplateColumns:'repeat(3,1fr)',gap:16}}>
        <div style={{padding:12,boxShadow:'0 1px 4px rgba(0,0,0,.12)'}}>Total alunos: <strong>--</strong></div>
        <div style={{padding:12,boxShadow:'0 1px 4px rgba(0,0,0,.12)'}}>Turmas ativas: <strong>--</strong></div>
        <div style={{padding:12,boxShadow:'0 1px 4px rgba(0,0,0,.12)'}}>Vagas disponíveis: <strong>--</strong></div>
      </section>
    </div>
  )
}
