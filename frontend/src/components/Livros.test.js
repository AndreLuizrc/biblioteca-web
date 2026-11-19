import { act } from 'react';
import { createRoot } from 'react-dom/client';
import { Simulate } from 'react-dom/test-utils';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import LivroForm from './LivroForm';
import LivroList from './LivroList';
import { autorService, livroService } from '../services/api';

jest.mock('../services/api', () => ({
  autorService: { listar: jest.fn() },
  livroService: { buscar: jest.fn(), criar: jest.fn(), atualizar: jest.fn(), listar: jest.fn(), deletar: jest.fn() },
  mensagemErro: () => 'Falha ao carregar livro ou autores.',
}));

let container;
let root;
beforeEach(() => {
  jest.resetAllMocks();
  global.IS_REACT_ACT_ENVIRONMENT = true;
  container = document.createElement('div');
  document.body.appendChild(container);
  root = createRoot(container);
  autorService.listar.mockResolvedValue({ data: [{ id: 1, nome: 'Machado' }, { id: 2, nome: 'Clarice' }] });
});
afterEach(async () => {
  await act(async () => root.unmount());
  container.remove();
  jest.restoreAllMocks();
});

async function formulario(rota = '/livros/novo') {
  await act(async () => root.render(
    <MemoryRouter initialEntries={[rota]} future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Routes>
        <Route path="/livros" element={<p>Retornou para a lista</p>} />
        <Route path="/livros/novo" element={<LivroForm />} />
        <Route path="/livros/editar/:id" element={<LivroForm />} />
      </Routes>
    </MemoryRouter>
  ));
}

async function alterar(name, value) {
  await act(async () => Simulate.change(container.querySelector(`#${name}`), { target: { name, value } }));
}

test('cria livro com autor numérico, ano numérico e opcionais nulos', async () => {
  livroService.criar.mockResolvedValue({});
  await formulario();
  expect(container.querySelector('select').textContent).toContain('Machado');
  await alterar('titulo', '  Dom Casmurro  ');
  await alterar('autorId', '1');
  await alterar('anoPublicacao', '1899');
  await act(async () => Simulate.submit(container.querySelector('form')));
  expect(livroService.criar).toHaveBeenCalledWith({ titulo: 'Dom Casmurro', autor: { id: 1 }, anoPublicacao: 1899, isbn: null, genero: null });
  expect(container.textContent).toContain('Retornou para a lista');
});

test('edita livro, troca autor e limpa campos opcionais', async () => {
  livroService.buscar.mockResolvedValue({ data: { titulo: 'Livro', autor: { id: 1 }, isbn: '123', genero: 'Romance', anoPublicacao: 1900 } });
  livroService.atualizar.mockResolvedValue({});
  await formulario('/livros/editar/7');
  expect(container.querySelector('#autorId').value).toBe('1');
  for (const campo of ['isbn', 'genero', 'anoPublicacao']) await alterar(campo, '');
  await alterar('autorId', '2');
  await act(async () => Simulate.submit(container.querySelector('form')));
  expect(livroService.atualizar).toHaveBeenCalledWith('7', { titulo: 'Livro', autor: { id: 2 }, isbn: null, genero: null, anoPublicacao: null });
});

test('bloqueia envio sem autores e oferece cadastro', async () => {
  autorService.listar.mockResolvedValue({ data: [] });
  await formulario();
  expect(container.textContent).toContain('Cadastre um autor');
  expect(container.querySelector('button[type="submit"]').disabled).toBe(true);
  await act(async () => Simulate.submit(container.querySelector('form')));
  expect(livroService.criar).not.toHaveBeenCalled();
});

test('bloqueia edição quando o livro não carrega', async () => {
  livroService.buscar.mockRejectedValue({ response: { status: 404 } });
  await formulario('/livros/editar/999');
  expect(container.querySelector('[role="alert"]')).not.toBeNull();
  expect(container.querySelector('button[type="submit"]').disabled).toBe(true);
  await act(async () => Simulate.submit(container.querySelector('form')));
  expect(livroService.atualizar).not.toHaveBeenCalled();
});

test('bloqueia criação quando a lista de autores falha', async () => {
  autorService.listar.mockRejectedValue(new Error('offline'));
  await formulario();
  expect(container.querySelector('button[type="submit"]').disabled).toBe(true);
  expect(container.querySelector('[role="alert"]')).not.toBeNull();
});

test('mostra autor na tabela e confirma exclusão', async () => {
  livroService.listar.mockResolvedValue({ data: [{ id: 7, titulo: 'Dom Casmurro', autor: { nome: 'Machado' } }] });
  livroService.deletar.mockResolvedValue({});
  jest.spyOn(window, 'confirm').mockReturnValue(true);
  await act(async () => root.render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}><LivroList /></MemoryRouter>));
  expect(container.textContent).toContain('Machado');
  await act(async () => Simulate.click(container.querySelector('button[aria-label="Excluir Dom Casmurro"]')));
  expect(livroService.deletar).toHaveBeenCalledWith(7);
  expect(container.textContent).toContain('0 livros');
});
