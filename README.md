# Biblioteca Web

Aplicação do laboratório de Engenharia de Software para cadastrar Autores e Livros.
Repositório público: [AndreLuizrc/biblioteca-web](https://github.com/AndreLuizrc/biblioteca-web).
Revisão do DEV 1: [PR #1](https://github.com/AndreLuizrc/biblioteca-web/pull/1).
O PR #1 de Autores foi integrado à `main`. A branch `feature/livros`, criada a partir
desse merge, implementa a parte do DEV 2: API e interface de Livros, seleção de autor,
testes de controller e integração Autor–Livro. Consulte o
[acompanhamento do DEV 2](docs/andamento-dev2.md) para validações e pendências de entrega.

## Tecnologias

| Camada | Tecnologias |
| --- | --- |
| Backend | Java 17, Spring Boot 3.2.12, Maven, JPA, Validation, Lombok |
| Frontend | React 18.3.1, React Router 6, Axios, react-scripts 5, Node 20, npm |
| Banco | PostgreSQL 15; H2 em memória no perfil `test` |
| Testes | JUnit 5, Mockito, MockMvc, SpringBootTest e Jest |
| Infraestrutura | Docker Compose, Nginx e GitHub Actions |

## Divisão de trabalho

| Integrante | Branch | Responsabilidade |
| --- | --- | --- |
| DEV 1 — André Luiz Rocha | `feature/autores` | Autor, repository, controller, testes, lista e formulário; estrutura e configurações compartilhadas |
| DEV 2 — Marcella Santos | `feature/livros` | Livro, repository, controller, testes, lista, formulário com select; `IntegracaoTest` e validação final do CI |

Turma: a preencher. Cada integrante deve revisar, deixar pelo menos um comentário e
fazer o merge do PR do colega. Não apagar as branches antes da avaliação.

## Estrutura

```text
backend/
  pom.xml, mvnw, mvnw.cmd, Dockerfile
  src/main/java/com/biblioteca/{model,repository,controller}
  src/main/resources/application.properties
  src/test/java/com/biblioteca/
  src/test/resources/application-test.properties
frontend/
  package.json, package-lock.json, Dockerfile, nginx.conf
  public/index.html
  src/{components,services}, App.js, App.css
.github/workflows/ci-cd.yml
docker-compose.yml
docs/
```

Consulte a [spec](docs/spec-implementacao-biblioteca-web.md) para todos os requisitos e
o [acompanhamento do DEV 1](docs/andamento-dev1.md) para validações e pendências.

## Executar com Docker Compose

Requisito: Docker com o engine iniciado e Docker Compose disponível.

Na raiz:

```powershell
docker compose up -d --build
docker compose ps
```

- Interface: <http://localhost:3000/autores>.
- API: <http://localhost:8080/api/autores>.
- PostgreSQL: `localhost:5432`, banco `biblioteca_db`, usuário/senha de laboratório `postgres/postgres`.

O Nginx encaminha `/api` para o backend e permite abrir diretamente as rotas React.
O volume `postgres_data` persiste o banco entre reinicializações. `docker compose down`
encerra os serviços preservando esse volume. Se o ambiente usar o comando legado,
substitua `docker compose` por `docker-compose`.

## Executar localmente

Requisitos: JDK **17**, Node.js **20** e PostgreSQL **15**. A `.nvmrc` registra a linha
do Node. Configure `JAVA_HOME` para o JDK 17; o Java padrão do computador pode ser outro.
O Maven Wrapper baixa Maven na primeira execução, exigindo acesso à internet.

1. Na raiz, suba somente o banco: `docker compose up -d postgres`.
2. Em `backend/`, execute `./mvnw.cmd spring-boot:run` no Windows ou `./mvnw spring-boot:run` no Linux/macOS.
3. Em outro terminal, dentro de `frontend/`:

```powershell
npm ci
npm start
```

A API usa a porta 8080 e o React a 3000. Opcionalmente copie `frontend/.env.example`
para `frontend/.env.local` para configurar `REACT_APP_API_URL` e reinicie o frontend.
O backend aceita `DB_URL`, `DB_USERNAME` e `DB_PASSWORD`; os valores padrão são os do
PostgreSQL local descrito acima. No Compose, o banco é acessado pelo host `postgres`.

## Testes e build

Em `backend/`, com Java 17:

```powershell
./mvnw.cmd test -Dtest="*ControllerTest"
./mvnw.cmd test
./mvnw.cmd package
```

Em Linux/macOS, use `./mvnw`. Se houver Maven instalado, `mvn` é equivalente.
Os testes de persistência usam H2 em memória pelo perfil `test`, sem PostgreSQL externo.
`AutorControllerTest` cobre criação, busca, atualização, exclusão, 404 e validação.
`AutorPersistenciaTest` verifica CRUD real, ordenação e preservação de `criadoEm`.
`LivroControllerTest` verifica o CRUD, os limites dos campos e autores inválidos.
`IntegracaoTest` verifica o vínculo persistido, a troca de autor, a limpeza dos opcionais,
a preservação de `criadoEm`, a exclusão e a ordenação por título usando H2 real.

Em `frontend/`:

```powershell
npm ci
npm test -- --watchAll=false --passWithNoTests
npm run build
```

Os testes Jest verificam criação, edição com limpeza de opcionais, falha de carregamento
e exclusão. O workflow executa os testes backend e frontend e o build React em PRs
destinados à `main` e pushes na `main`. Sua execução no GitHub depende da publicação
do repositório; o YAML local sozinho não comprova Actions verde.

## API e telas de Autores

| Método | Endpoint | Resposta |
| --- | --- | --- |
| POST | `/api/autores` | 201 e autor criado |
| GET | `/api/autores` | 200, lista ordenada por nome |
| GET | `/api/autores/{id}` | 200 ou 404 |
| PUT | `/api/autores/{id}` | 200 ou 404 |
| DELETE | `/api/autores/{id}` | 200 ou 404 |

Corpo de POST/PUT:

```json
{"nome":"Machado de Assis","nacionalidade":"Brasileira","dataNascimento":"1839-06-21"}
```

`nome` é obrigatório e tem até 100 caracteres; nacionalidade é opcional, com até 50;
data é opcional e segue `AAAA-MM-DD`. Valores inválidos retornam 400. ID e `criadoEm`
são definidos pelo backend; o PUT preserva esses dados. Campos opcionais aceitam `null`.
Não há coleção de livros em Autor, preservando o relacionamento unidirecional planejado.

Rotas de Autores: `/autores`, `/autores/novo` e `/autores/editar/:id`.
Rotas de Livros: `/livros`, `/livros/novo` e `/livros/editar/:id`.
A rota `/` redireciona para `/autores`.

## API e telas de Livros

| Método | Endpoint | Resposta |
| --- | --- | --- |
| POST | `/api/livros` | 201 e livro criado com autor |
| GET | `/api/livros` | 200, lista ordenada por título |
| GET | `/api/livros/{id}` | 200 ou 404 |
| PUT | `/api/livros/{id}` | 200 ou 404 |
| DELETE | `/api/livros/{id}` | 200 ou 404 |

Exemplo de POST/PUT, usando um ID de autor cadastrado:

```json
{"titulo":"Dom Casmurro","isbn":"978-8535910667","anoPublicacao":1899,"genero":"Romance","autor":{"id":1}}
```

Título é obrigatório, com até 200 caracteres. ISBN (até 20), gênero (até 50) e ano
inteiro são opcionais. Autor ausente ou sem ID retorna 400; ID de autor inexistente
retorna 404. POST e PUT resolvem o autor no banco; PUT permite trocar o autor e limpar
opcionais. ID e data de criação são definidos pelo backend e preservados na edição.
O formulário carrega autores pela API e bloqueia envio quando não há autores ou o
carregamento falha. A tabela mostra o nome do autor. Exclua os livros antes do autor:
a FK protege os vínculos, sem exclusão em cascata.

## Publicação e revisão

O PR #1 de Autores foi integrado no commit `49a0b3d`. A implementação local do DEV 2
está em `feature/livros`. Para publicar a branch e abrir seu PR após os testes:

```powershell
git push -u origin feature/livros
```

O PR de Livros pode usar o [texto preparado](docs/pr-livros.md). DEV 1 deve revisar,
deixar um comentário e fazer o merge com os checks verdes. O workflow existente já
executa os novos testes junto dos testes de Autores. Após o merge, ambos atualizam
sua `main` e coletam a execução verde do Actions da versão final. Preencher a turma
e completar as evidências antes da entrega.
