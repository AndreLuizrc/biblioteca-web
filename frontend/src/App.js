import { BrowserRouter, Link, Navigate, NavLink, Route, Routes } from 'react-router-dom';
import AutorList from './components/AutorList';
import AutorForm from './components/AutorForm';
import './App.css';

export default function App() {
  return (
    <BrowserRouter>
      <a className="skip-link" href="#conteudo">Ir para o conteúdo</a>
      <header className="site-header">
        <div className="header-inner">
          <Link className="brand" to="/autores" aria-label="Biblioteca Web, início">
            <span className="brand-symbol" aria-hidden="true">B.</span>
            <span>Biblioteca <span className="brand-light">Web</span></span>
          </Link>
          <nav aria-label="Navegação principal"><NavLink to="/autores">Autores</NavLink></nav>
        </div>
      </header>
      <main id="conteudo" className="main-content">
        <Routes>
          <Route path="/" element={<Navigate to="/autores" replace />} />
          <Route path="/autores" element={<AutorList />} />
          <Route path="/autores/novo" element={<AutorForm />} />
          <Route path="/autores/editar/:id" element={<AutorForm />} />
          <Route path="*" element={<section className="empty-state"><h1>Página não encontrada</h1><Link to="/autores">Voltar para Autores</Link></section>} />
        </Routes>
      </main>
      <footer className="site-footer">Biblioteca Web <span aria-hidden="true">·</span> Cada história começa com uma pessoa.</footer>
    </BrowserRouter>
  );
}
