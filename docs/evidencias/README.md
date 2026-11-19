# Evidências da Biblioteca Web

Capturas reais feitas em 19/11/2026, com frontend React de produção e backend
Spring Boot em Java 17 usando H2 temporário.

- [Lista de Autores](03-autores-lista.png): dois registros criados no teste e retornados em ordem de nome.
- [Formulário de Autores](04-autores-formulario.png): formulário preenchido antes do POST.
- [Resultados de validação](validacao-dev1.txt): trechos dos logs reais de build/testes e verificação no navegador.
- [CI do DEV 1](08-actions-dev1.png): captura real da [execução verde do PR #1](https://github.com/AndreLuizrc/biblioteca-web/actions/runs/44516447389), commit `9c847df`.

## DEV 2 — Livros

- [Lista de Livros](05-livros-lista.png): livro vinculado com nome do autor na tabela.
- [Formulário de Livros](06-livros-formulario.png): dados preenchidos e autor selecionado.
- [Resposta real da API](api-vinculo-dev2.json): POST de Livro com status 201 e vínculo.
- [Resultados de validação](validacao-dev2.txt): testes backend/React, build e fluxo no navegador.

Capturas do DEV 2 feitas com build React de produção e API real Java 17 usando H2
temporário nas portas de teste 18000/18080. Fluxo desktop/mobile verificado.

Os registros das capturas foram usados apenas para teste e removidos depois. A
validação em H2 não comprova a execução com PostgreSQL/containers. O print do
terminal, o print da ferramenta de API com vínculo e o Actions da versão
integrada em `main` ainda devem ser coletados para a entrega completa.
