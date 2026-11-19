## CRUD de Livros e integração com Autores

Adiciona os cinco endpoints de Livros, entidade JPA e repository com ordenação por
título e busca por autor. Criação e edição vinculam um autor existente; o PUT permite
trocar o autor e limpar campos opcionais, preservando ID e data de criação.

A interface inclui lista com nome do autor, criação, edição e exclusão, serviço Axios,
menu e três rotas. O formulário carrega autores e impede envio sem autor disponível
ou quando os dados falham ao carregar.

Validação: 29 testes Maven e 10 testes React passando; build de produção compilado
com sucesso. CRUD verificado no Edge com API real/H2, incluindo troca de autor,
opcionais, exclusão e estados de erro. Capturas das telas e resultados registrados
em `docs/andamento-dev2.md`.

Revisão obrigatória do DEV 1: deixar comentário, conferir os checks do CI e integrar
preservando a branch até a avaliação. Actions da `main` final deve ser capturado após
o merge.
