import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { autorService, livroService, mensagemErro } from '../services/api';

const vazio = { titulo: '', isbn: '', anoPublicacao: '', genero: '', autorId: '' };

export default function LivroForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [livro, setLivro] = useState(vazio);
  const [autores, setAutores] = useState([]);
  const [carregando, setCarregando] = useState(true);
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState('');
  const [falha, setFalha] = useState(false);

  useEffect(() => {
    let ativo = true;
    setCarregando(true);
    setErro('');
    setFalha(false);
    setLivro(vazio);
    setAutores([]);
    Promise.all([autorService.listar(), id ? livroService.buscar(id) : Promise.resolve(null)])
      .then(([lista, resposta]) => {
        if (!ativo) return;
        setAutores(lista.data);
        if (resposta) {
          const data = resposta.data;
          setLivro({ titulo: data.titulo, isbn: data.isbn || '', anoPublicacao: data.anoPublicacao ?? '', genero: data.genero || '', autorId: String(data.autor.id) });
        }
      }).catch((e) => {
        if (ativo) { setErro(mensagemErro(e, 'Livro')); setFalha(true); }
      }).finally(() => { if (ativo) setCarregando(false); });
    return () => { ativo = false; };
  }, [id]);

  function alterar(event) {
    const { name, value } = event.target;
    setLivro((atual) => ({ ...atual, [name]: value }));
  }

  async function salvar(event) {
    event.preventDefault();
    if (salvando || carregando || falha || autores.length === 0) return;
    if (!livro.titulo.trim() || !autores.some((autor) => String(autor.id) === livro.autorId)) {
      setErro('Informe o título e selecione um autor.');
      return;
    }
    const ano = livro.anoPublicacao === '' ? null : Number(livro.anoPublicacao);
    if (ano !== null && (!Number.isInteger(ano) || ano < -2147483648 || ano > 2147483647)) {
      setErro('Informe um ano inteiro válido.');
      return;
    }
    const dados = { titulo: livro.titulo.trim(), isbn: livro.isbn.trim() || null, anoPublicacao: ano, genero: livro.genero.trim() || null, autor: { id: Number(livro.autorId) } };
    setSalvando(true);
    setErro('');
    try {
      if (id) await livroService.atualizar(id, dados);
      else await livroService.criar(dados);
      navigate('/livros');
    } catch (e) { setErro(mensagemErro(e, 'Livro')); }
    finally { setSalvando(false); }
  }

  const bloqueado = salvando || carregando || falha || autores.length === 0;
  return (
    <div className="form-page">
      <Link className="back-link" to="/livros">← Voltar para livros</Link>
      <p className="eyebrow">CATÁLOGO / LIVROS / {id ? 'EDITAR' : 'NOVO'}</p>
      <div className="page-heading"><div><h1>{id ? 'Editar livro.' : 'Uma nova história na estante.'}</h1><p className="subtitle">Preencha os dados do livro e escolha seu autor.</p></div></div>
      {erro && <div className="alert" role="alert">{erro}</div>}
      {!carregando && !falha && autores.length === 0 && <div className="alert" role="status">Cadastre um autor antes de adicionar livros. <Link to="/autores/novo">Cadastrar autor</Link></div>}
      <section className="panel" aria-labelledby="titulo-formulario">
        <div className="panel-heading"><h2 id="titulo-formulario">Informações do livro</h2><span className="optional-note">* Campo obrigatório</span></div>
        {carregando ? <p className="empty-state" role="status">Carregando dados…</p> : <form onSubmit={salvar}>
          <fieldset disabled={bloqueado} className="form-fields">
            <div className="field"><label htmlFor="titulo">Título *</label><input id="titulo" name="titulo" value={livro.titulo} onChange={alterar} required maxLength={200} placeholder="Ex.: Dom Casmurro" /></div>
            <div className="form-grid">
              <div className="field"><label htmlFor="autorId">Autor *</label><select id="autorId" name="autorId" value={livro.autorId} onChange={alterar} required><option value="">Selecione um autor</option>{autores.map((autor) => <option key={autor.id} value={autor.id}>{autor.nome}</option>)}</select></div>
              <div className="field"><label htmlFor="isbn">ISBN <span className="optional">opcional</span></label><input id="isbn" name="isbn" value={livro.isbn} onChange={alterar} maxLength={20} /></div>
              <div className="field"><label htmlFor="anoPublicacao">Ano de publicação <span className="optional">opcional</span></label><input id="anoPublicacao" name="anoPublicacao" type="number" step="1" min="-2147483648" max="2147483647" value={livro.anoPublicacao} onChange={alterar} /></div>
              <div className="field"><label htmlFor="genero">Gênero <span className="optional">opcional</span></label><input id="genero" name="genero" value={livro.genero} onChange={alterar} maxLength={50} /></div>
            </div>
          </fieldset>
          <div className="form-footer"><Link className="button button-secondary" to="/livros">Cancelar</Link><button className="button button-primary" type="submit" disabled={bloqueado}>{salvando ? 'Salvando…' : 'Salvar livro'}</button></div>
        </form>}
      </section>
      <p className="page-note">Os campos opcionais podem ser preenchidos depois.</p>
    </div>
  );
}
