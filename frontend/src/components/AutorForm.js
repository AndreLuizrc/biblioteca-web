import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { autorService, mensagemErro } from '../services/api';

const vazio = { nome: '', nacionalidade: '', dataNascimento: '' };

export default function AutorForm() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [autor, setAutor] = useState(vazio);
  const [carregando, setCarregando] = useState(Boolean(id));
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState('');
  const [falhaAoCarregar, setFalhaAoCarregar] = useState(false);

  useEffect(() => {
    let ativo = true;
    setErro('');
    setFalhaAoCarregar(false);
    setAutor(vazio);
    setCarregando(Boolean(id));
    if (id) {
      autorService.buscar(id).then(({ data }) => {
        if (ativo) setAutor({ nome: data.nome, nacionalidade: data.nacionalidade || '', dataNascimento: data.dataNascimento || '' });
      }).catch((e) => {
        if (ativo) { setErro(mensagemErro(e)); setFalhaAoCarregar(true); }
      }).finally(() => { if (ativo) setCarregando(false); });
    }
    return () => { ativo = false; };
  }, [id]);

  function alterar(event) {
    const { name, value } = event.target;
    setAutor((atual) => ({ ...atual, [name]: value }));
  }

  async function salvar(event) {
    event.preventDefault();
    if (salvando || falhaAoCarregar) return;
    if (!autor.nome.trim()) { setErro('Informe o nome do autor.'); return; }
    setSalvando(true);
    setErro('');
    const dados = {
      nome: autor.nome.trim(),
      nacionalidade: autor.nacionalidade.trim() || null,
      dataNascimento: autor.dataNascimento || null,
    };
    try {
      if (id) await autorService.atualizar(id, dados);
      else await autorService.criar(dados);
      navigate('/autores');
    } catch (e) {
      setErro(mensagemErro(e));
    } finally {
      setSalvando(false);
    }
  }

  return (
    <div className="form-page">
      <Link className="back-link" to="/autores">← Voltar para autores</Link>
      <p className="eyebrow">CATÁLOGO / AUTORES / {id ? 'EDITAR' : 'NOVO'}</p>
      <div className="page-heading"><div><h1>{id ? 'Editar autor.' : 'Um novo nome, novas histórias.'}</h1><p className="subtitle">{id ? 'Atualize as informações deste autor.' : 'Adicione ao catálogo quem dá vida às palavras.'}</p></div></div>
      {erro && <div className="alert" role="alert">{erro}</div>}
      <section className="panel" aria-labelledby="titulo-formulario">
        <div className="panel-heading"><h2 id="titulo-formulario">Informações do autor</h2><span className="optional-note">* Campo obrigatório</span></div>
        {carregando ? <p className="empty-state" role="status">Carregando autor…</p> : <form onSubmit={salvar}>
          <fieldset disabled={salvando || falhaAoCarregar} className="form-fields">
            <div className="field"><label htmlFor="nome">Nome completo <span aria-hidden="true">*</span></label><input id="nome" name="nome" value={autor.nome} onChange={alterar} required maxLength={100} autoComplete="name" placeholder="Ex.: Machado de Assis" /></div>
            <div className="form-grid">
              <div className="field"><label htmlFor="nacionalidade">Nacionalidade <span className="optional">opcional</span></label><input id="nacionalidade" name="nacionalidade" value={autor.nacionalidade} onChange={alterar} maxLength={50} placeholder="Ex.: Brasileira" /></div>
              <div className="field"><label htmlFor="dataNascimento">Data de nascimento <span className="optional">opcional</span></label><input id="dataNascimento" name="dataNascimento" type="date" value={autor.dataNascimento} onChange={alterar} /></div>
            </div>
          </fieldset>
          <div className="form-footer"><Link className="button button-secondary" to="/autores">Cancelar</Link><button className="button button-primary" type="submit" disabled={salvando || falhaAoCarregar}>{salvando ? 'Salvando…' : 'Salvar autor'}</button></div>
        </form>}
      </section>
      <p className="page-note">Os campos opcionais podem ser preenchidos depois.</p>
    </div>
  );
}
