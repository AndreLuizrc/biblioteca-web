# feat: implementa CRUD de Autores e configura a Biblioteca Web

Implementa cadastro, consulta, edição e exclusão de Autores na API Spring Boot e nas
telas React. A listagem vem ordenada por nome e o formulário aceita nacionalidade
e nascimento opcionais. Requisições inválidas retornam 400 e IDs inexistentes, 404.

A persistência gera ID e timestamp e mantém esses dados nas atualizações.
O mapeamento JDBC direto de LocalDate preserva datas históricas no H2.

Inclui configuração compartilhada para PostgreSQL/H2, Maven Wrapper, Node 20,
Docker/Nginx/Compose, workflow GitHub Actions e instruções no README.

## Validação

- Maven Wrapper/Java 17: `package` com BUILD SUCCESS, 13 testes backend passando e JAR gerado.
- Node 20: `npm ci`, 4 testes Jest passando e build React compilado com sucesso.
- Navegador com API real/H2: criação, listagem ordenada, edição, limpeza de opcionais, exclusão e 404.
- Prints reais de lista e formulário em `docs/evidencias/`.
- Compose validado sintaticamente; containers não executados porque o engine Docker estava desligado.

## Revisão do DEV 2

Conferir endpoints/status, ordenação, validações, atualização de opcionais e o fluxo
das telas. Deixar pelo menos um comentário de revisão antes de fazer o merge.

Livros e `IntegracaoTest.deveVincularLivroAAutor` serão implementados em `feature/livros`
após incorporar esta base. A execução do Actions deve ser confirmada no GitHub;
nenhuma execução remota é presumida a partir dos testes locais.
