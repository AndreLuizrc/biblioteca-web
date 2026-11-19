import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { autorService, mensagemErro } from '../services/api';

function formatarData(data) {
  return data ? data.split('-').reverse().join('/') : '—';
}

export default function AutorList() {
  const [autores, setAutores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState('');
  const [excluindo, setExcluindo] = useState(null);
  const [tentativa, setTentativa] = useState(0);

  useEffect(() => {
    let ativo = true;
    setCarregando(true);
    setErro('');
    autorService.listar().then(({ data }) => {
      if (ativo) setAutores(data);
    }).catch((e) => {
      if (ativo) setErro(mensagemErro(e));
    }).finally(() => {
      if (ativo) setCarregando(false);
    });
    return () => { ativo = false; };
  }, [tentativa]);

  async function excluir(autor) {
    if (!window.confirm(`Excluir o autor “${autor.nome}”? Esta ação não pode ser desfeita.`)) return;
    setExcluindo(autor.id);
    setErro('');
    try {
      await autorService.deletar(autor.id);
      setAutores((atuais) => atuais.filter((item) => item.id !== autor.id));
    } catch (e) {
      setErro(mensagemErro(e));
    } finally {
      setExcluindo(null);
    }
  }

  return (
    <>
      <p className="eyebrow">CATÁLOGO / AUTORES</p>
      <div className="page-heading">
        <div><h1>Quem escreve as histórias.</h1><p className="subtitle">Organize os autores que fazem parte da sua biblioteca.</p></div>
        <Link className="button button-primary" to="/autores/novo"><span aria-hidden="true">＋</span> Novo autor</Link>
      </div>
      {erro && <div className="alert" role="alert">{erro} <button className="text-button" onClick={() => setTentativa((n) => n + 1)}>Tentar novamente</button></div>}
      <section className="panel" aria-labelledby="titulo-lista">
        <div className="panel-heading"><h2 id="titulo-lista">Autores cadastrados</h2><span className="count">{carregando ? 'Carregando…' : `${autores.length} ${autores.length === 1 ? 'autor' : 'autores'}`}</span></div>
        {carregando ? <p className="empty-state" role="status">Carregando autores…</p> : autores.length === 0 ? (
          <div className="empty-state"><span className="empty-mark" aria-hidden="true">Aa</span><h3>Uma biblioteca de possibilidades.</h3><p>Cadastre o primeiro autor para começar seu catálogo.</p><Link className="text-link" to="/autores/novo">Cadastrar autor <span aria-hidden="true">→</span></Link></div>
        ) : (
          <div className="table-scroll"><table>
            <thead><tr><th scope="col">Nome</th><th scope="col">Nacionalidade</th><th scope="col">Nascimento</th><th scope="col" className="actions-cell">Ações</th></tr></thead>
            <tbody>{autores.map((autor) => <tr key={autor.id}>
              <td className="author-name">{autor.nome}</td><td>{autor.nacionalidade || '—'}</td><td>{formatarData(autor.dataNascimento)}</td>
              <td className="actions-cell"><div className="row-actions"><Link to={`/autores/editar/${autor.id}`} aria-label={`Editar ${autor.nome}`}>Editar</Link><button className="text-button danger" disabled={excluindo !== null} onClick={() => excluir(autor)} aria-label={`Excluir ${autor.nome}`}>{excluindo === autor.id ? 'Excluindo…' : 'Excluir'}</button></div></td>
            </tr>)}</tbody>
          </table></div>
        )}
      </section>
      <p className="page-note">Em ordem alfabética, para encontrar cada nome com facilidade.</p>
    </>
  );
}
