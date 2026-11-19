import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { livroService, mensagemErro } from '../services/api';

export default function LivroList() {
  const [livros, setLivros] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [excluindo, setExcluindo] = useState(null);
  const [tentativa, setTentativa] = useState(0);

  useEffect(() => {
    let ativo = true;
    setCarregando(true);
    setErro('');
    livroService.listar().then(({ data }) => {
      if (ativo) setLivros(data);
    }).catch((e) => {
      if (ativo) setErro(mensagemErro(e, 'Livro'));
    }).finally(() => { if (ativo) setCarregando(false); });
    return () => { ativo = false; };
  }, [tentativa]);

  async function excluir(livro) {
    if (!window.confirm(`Excluir o livro “${livro.titulo}”? Esta ação não pode ser desfeita.`)) return;
    setExcluindo(livro.id);
    setErro('');
    try {
      await livroService.deletar(livro.id);
      setLivros((atuais) => atuais.filter((item) => item.id !== livro.id));
    } catch (e) { setErro(mensagemErro(e, 'Livro')); }
    finally { setExcluindo(null); }
  }

  return (
    <>
      <p className="eyebrow">CATÁLOGO / LIVROS</p>
      <div className="page-heading"><div><h1>Histórias na sua estante.</h1><p className="subtitle">Organize seus livros e encontre quem escreveu cada história.</p></div><Link className="button button-primary" to="/livros/novo"><span aria-hidden="true">＋</span> Novo livro</Link></div>
      {erro && <div className="alert" role="alert">{erro} <button className="text-button" onClick={() => setTentativa((n) => n + 1)}>Tentar novamente</button></div>}
      <section className="panel" aria-labelledby="titulo-lista">
        <div className="panel-heading"><h2 id="titulo-lista">Livros cadastrados</h2><span className="count">{carregando ? 'Carregando…' : `${livros.length} ${livros.length === 1 ? 'livro' : 'livros'}`}</span></div>
        {carregando ? <p className="empty-state" role="status">Carregando livros…</p> : livros.length === 0 ? (
          <div className="empty-state"><h3>Sua próxima leitura começa aqui.</h3><p>Cadastre o primeiro livro para começar sua coleção.</p><Link className="text-link" to="/livros/novo">Cadastrar livro →</Link></div>
        ) : <div className="table-scroll"><table>
          <thead><tr><th scope="col">Título</th><th scope="col">Autor</th><th scope="col">ISBN</th><th scope="col">Ano</th><th scope="col">Gênero</th><th scope="col" className="actions-cell">Ações</th></tr></thead>
          <tbody>{livros.map((livro) => <tr key={livro.id}>
            <td className="author-name">{livro.titulo}</td><td>{livro.autor.nome}</td><td>{livro.isbn || '—'}</td><td>{livro.anoPublicacao ?? '—'}</td><td>{livro.genero || '—'}</td>
            <td className="actions-cell"><div className="row-actions"><Link to={`/livros/editar/${livro.id}`} aria-label={`Editar ${livro.titulo}`}>Editar</Link><button className="text-button danger" disabled={excluindo !== null} onClick={() => excluir(livro)} aria-label={`Excluir ${livro.titulo}`}>{excluindo === livro.id ? 'Excluindo…' : 'Excluir'}</button></div></td>
          </tr>)}</tbody>
        </table></div>}
      </section>
      <p className="page-note">Em ordem alfabética de título, para encontrar sua próxima história.</p>
    </>
  );
}
