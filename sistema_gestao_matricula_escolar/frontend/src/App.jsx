import React from 'react'
import { Routes, Route, Link } from 'react-router-dom'
import Dashboard from './pages/Dashboard'
import Alunos from './pages/Alunos'

export default function App(){
  return (
    <div style={{padding:20}}>
      <header style={{display:'flex',gap:12,alignItems:'center'}}>
        <h2>Gestão Matrícula</h2>
        <nav><Link to='/'>Dashboard</Link> | <Link to='/alunos'>Alunos</Link></nav>
      </header>
      <main style={{marginTop:20}}>
        <Routes>
          <Route path='/' element={<Dashboard/>} />
          <Route path='/alunos' element={<Alunos/>} />
        </Routes>
      </main>
    </div>
  )
}
