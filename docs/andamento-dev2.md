# Acompanhamento do DEV 2

Branch: `feature/livros`, criada a partir de `main` no merge `49a0b3d` do PR #1.

## Implementação

- `Livro`: campos e limites do enunciado, FK obrigatória e unidirecional para Autor,
  sem cascata; ID gerado e timestamp automático somente de leitura.
- `LivroRepository`: CRUD, `findAllByOrderByTituloAsc` e `findByAutorId`.
- `LivroController`: POST/GET/PUT/DELETE, 201/200/400/404 e autor resolvido no banco.
- `LivroList` e `LivroForm`: tabela com nome do autor, seleção obrigatória via API,
  criação/edição/exclusão, opcionais nulos, estados de carregamento e erro.
- Menu, três rotas de Livros e serviço Axios integrados à base de Autores.
- Testes de controller, integração com H2 e testes React da funcionalidade.
- Docker/Compose e workflow recebidos da base do DEV 1 continuam cobrindo o conjunto;
  o job Maven descobre automaticamente o `IntegracaoTest`.

## Validação local

- Java 17.0.21 e Maven 3.9.9: `mvn -B test` — **BUILD SUCCESS**, 29 testes,
  zero falhas/erros: AutorPersistenciaTest (1), AutorControllerTest (12),
  LivroControllerTest (14), IntegracaoTest (2).
- Node 20.19.0: testes Jest — **2 suítes, 10 testes passando**.
- Build React de produção — **Compiled successfully**.
- Edge headless com build de produção e API Java 17/H2 temporário: criação,
  edição, troca de autor, limpeza dos opcionais, preservação do timestamp,
  exclusão, 404 e bloqueio sem autores passaram. Verificação desktop/mobile,
  sem erros JavaScript. Registros criados para teste foram removidos.
- Capturas reais: [lista](evidencias/05-livros-lista.png) e
  [formulário](evidencias/06-livros-formulario.png). Resposta real do POST:
  [api-vinculo-dev2.json](evidencias/api-vinculo-dev2.json).
- [Resumo dos resultados](evidencias/validacao-dev2.txt).

## Pendências de processo e entrega

- Publicar a branch e abrir PR para `main`; descrição em [pr-livros.md](pr-livros.md).
- DEV 1 revisa e deixa comentário; merge somente após CI verde.
- Capturar Actions da `main` final e evidências restantes exigidas na spec.
- Preencher nomes reais da dupla e turma no README.

Nenhuma revisão ou execução remota do DEV 2 foi declarada concluída nesta etapa.
