import axios from 'axios';

const api = axios.create({
  baseURL: process.env.REACT_APP_API_URL || 'http://localhost:8080/api',
  timeout: 15000,
});

export const autorService = {
  listar: () => api.get('/autores'),
  buscar: (id) => api.get(`/autores/${id}`),
  criar: (autor) => api.post('/autores', autor),
  atualizar: (id, autor) => api.put(`/autores/${id}`, autor),
  deletar: (id) => api.delete(`/autores/${id}`),
};

export function mensagemErro(erro) {
  if (erro.response?.status === 404) return 'Autor não encontrado. Volte à lista e tente novamente.';
  if (erro.response?.status === 400) return 'Confira os campos informados e tente novamente.';
  if (erro.response?.status === 409) return 'Este autor possui livros vinculados. Exclua os livros primeiro.';
  return 'Não foi possível concluir a operação. Confira a conexão com a API e tente novamente.';
}

export default api;
