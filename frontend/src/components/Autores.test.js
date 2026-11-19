import React, { act } from 'react';
import { createRoot } from 'react-dom/client';
import { Simulate } from 'react-dom/test-utils';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import AutorForm from './AutorForm';
import AutorList from './AutorList';
import { autorService } from '../services/api';

jest.mock('../services/api', () => ({
  autorService: {
    buscar: jest.fn(), criar: jest.fn(), atualizar: jest.fn(),
    listar: jest.fn(), deletar: jest.fn(),
  },
  mensagemErro: () => 'Autor não encontrado.',
}));

let container;
let root;

beforeEach(() => {
  jest.clearAllMocks();
  global.IS_REACT_ACT_ENVIRONMENT = true;
  container = document.createElement('div');
  document.body.appendChild(container);
  root = createRoot(container);
});

afterEach(async () => {
  await act(async () => root.unmount());
  container.remove();
  jest.restoreAllMocks();
});

async function formulario(rota) {
  await act(async () => root.render(
    <MemoryRouter initialEntries={[rota]} future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
      <Routes>
        <Route path="/autores" element={<p>Retornou para a lista</p>} />
        <Route path="/autores/novo" element={<AutorForm />} />
        <Route path="/autores/editar/:id" element={<AutorForm />} />
      </Routes>
    </MemoryRouter>
  ));
}

test('cria autor com campos opcionais vazios como null e volta para a lista', async () => {
  autorService.criar.mockResolvedValue({ data: { id: 1 } });
  await formulario('/autores/novo');
  await act(async () => Simulate.change(container.querySelector('#nome'), {
    target: { name: 'nome', value: '  Machado de Assis  ' },
  }));
  await act(async () => Simulate.submit(container.querySelector('form')));
  expect(autorService.criar).toHaveBeenCalledWith({
    nome: 'Machado de Assis', nacionalidade: null, dataNascimento: null,
  });
  expect(container.textContent).toContain('Retornou para a lista');
});

test('carrega autor e permite limpar dados opcionais na edição', async () => {
  autorService.buscar.mockResolvedValue({ data: {
    id: 7, nome: 'Machado', nacionalidade: 'Brasileira', dataNascimento: '1839-06-21',
  } });
  autorService.atualizar.mockResolvedValue({});
  await formulario('/autores/editar/7');
  expect(container.querySelector('#dataNascimento').value).toBe('1839-06-21');
  for (const name of ['nacionalidade', 'dataNascimento']) {
    await act(async () => Simulate.change(container.querySelector(`#${name}`), { target: { name, value: '' } }));
  }
  await act(async () => Simulate.submit(container.querySelector('form')));
  expect(autorService.atualizar).toHaveBeenCalledWith('7', {
    nome: 'Machado', nacionalidade: null, dataNascimento: null,
  });
});

test('não permite salvar um autor que falhou ao carregar', async () => {
  autorService.buscar.mockRejectedValue({ response: { status: 404 } });
  await formulario('/autores/editar/999');
  expect(container.querySelector('[role="alert"]').textContent).toContain('Autor não encontrado');
  expect(container.querySelector('button[type="submit"]').disabled).toBe(true);
  await act(async () => Simulate.submit(container.querySelector('form')));
  expect(autorService.atualizar).not.toHaveBeenCalled();
});

test('confirma a exclusão e remove o autor da listagem', async () => {
  autorService.listar.mockResolvedValue({ data: [{ id: 1, nome: 'Machado', dataNascimento: '1839-06-21' }] });
  autorService.deletar.mockResolvedValue({});
  jest.spyOn(window, 'confirm').mockReturnValue(true);
  await act(async () => root.render(<MemoryRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}><AutorList /></MemoryRouter>));
  expect(container.textContent).toContain('21/06/1839');
  await act(async () => Simulate.click(container.querySelector('button[aria-label="Excluir Machado"]')));
  expect(autorService.deletar).toHaveBeenCalledWith(1);
  expect(container.textContent).toContain('0 autores');
});
