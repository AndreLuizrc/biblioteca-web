# Spec de implementação — Biblioteca Web

Data: 19/11/2026. Estado: planejamento; implementação e evidências ainda pendentes.

## 1. Objetivo, fontes e escopo

Construir, em dupla, uma aplicação web para CRUD de Autores e Livros, reproduzindo o fluxo da Agenda Web: modelagem, API, testes, interface, Git/GitHub com revisão, containers e CI.

Fontes lidas integralmente:

- [Relembrando Agenda Web](relembrando-agenda-web.pdf), páginas 1–4: contexto, arquitetura, tecnologias e fluxo de desenvolvimento.
- [Parte 2 — Laboratório](parte2-laboratorio.pdf), páginas 1–7: requisitos específicos, passos obrigatórios, extras, entrega e rubrica.

O enunciado da Parte 2 determina o escopo avaliado. As referências à Agenda orientam sua adaptação. AWS e JaCoCo aparecem no material de apoio, mas não são exigências da Parte 2.

O exercício acontece no laboratório, em dupla, com duração prevista de 2h30 a 3h e entrega até o fim da aula pelo canal indicado pelo professor. A Parte 2 vale 3 pontos; a Parte 1 vale 2. Ambos devem compreender também a implementação do colega.

Convenções desta spec:

- **Obrigatório:** exigência expressa do laboratório, inclusive processo e evidências.
- **Decisão técnica:** comportamento definido aqui para tornar a implementação executável; não é uma exigência adicional atribuída ao professor.
- **Extra:** bônus possível, somente após concluir os obrigatórios.

Situação inicial: o workspace contém os dois PDFs em `docs/`, sem aplicação ou repositório Git inicializado. Os arquivos-fonte da Agenda não estão disponíveis nesta pasta. Se acessíveis no laboratório, devem ser abertos, compreendidos e adaptados conforme o enunciado; os PDFs não substituem esses arquivos de referência.

## 2. Tecnologias obrigatórias e arquitetura

| Camada | Especificação |
| --- | --- |
| Backend | Java 17, Spring Boot **3.2.x**, Maven, Spring Web, Spring Data JPA, Validation e Lombok |
| Frontend | React **18**, React Router **6**, Axios, **react-scripts 5**, Node.js **20**, npm |
| Dados | PostgreSQL **15** em desenvolvimento; H2 em memória no perfil `test` |
| Testes backend | JUnit 5, Mockito, `@WebMvcTest`, `@MockBean`, MockMvc e `@SpringBootTest` |
| Testes frontend | Jest via react-scripts; o enunciado permite execução sem testes usando `--passWithNoTests` |
| Processo | Git, GitHub, Pull Requests e revisão cruzada |
| Infraestrutura | Docker, Docker Compose e GitHub Actions |

Fixar versões compatíveis com essas linhas e versionar `package-lock.json`. A criação do frontend deve usar Create React App conforme o enunciado, verificando que o resultado continua em React 18 e react-scripts 5. Evitar que versões padrão do gerador ou instalações sem restrição alterem a stack exigida. Não substituir CRA por Vite.

Arquitetura: `React → Axios/HTTP/JSON → Controller → JpaRepository → JPA/Hibernate → PostgreSQL`. Nos testes de integração, o H2 substitui o PostgreSQL. Nos containers, o frontend é servido pelo Nginx.

Usar `groupId=com.biblioteca`, `artifactId=biblioteca-web` e pacote base `com.biblioteca`. Manter a arquitetura simples da Agenda; camada de serviços, autenticação e paginação não são requisitos.

## 3. Divisão obrigatória e estratégia de branches

### 3.1 Responsáveis

Preencher os nomes reais da dupla e a turma no README antes da entrega.

| Responsável | Branch exata | Arquivos de sua funcionalidade |
| --- | --- | --- |
| DEV 1 | `feature/autores` | `Autor.java`, `AutorRepository.java`, `AutorController.java`, `AutorControllerTest.java`, `AutorList.js`, `AutorForm.js` |
| DEV 2 | `feature/livros` | `Livro.java`, `LivroRepository.java`, `LivroController.java`, `LivroControllerTest.java`, `LivroList.js`, `LivroForm.js` |

Os nomes das branches são obrigatórios. Criá-las depois de concluir toda a aplicação não demonstra a divisão pedida: os commits de cada funcionalidade devem ser feitos na branch correspondente, pelo integrante responsável.

Arquivos compartilhados podem ser divididos. **Decisão de coordenação:** DEV 1 inicia estrutura, configuração do banco, `api.js`, `App.js`, Compose e README; DEV 2 complementa serviços e rotas de Livros, cria `IntegracaoTest.java` e finaliza o workflow. Cada um implementa seu Dockerfile ou combina essa divisão explicitamente. Registrar a divisão real no README.

### 3.2 Inicialização

DEV 1 cria o esqueleto e o `.gitignore` (ao menos `target/` e `node_modules/`), inicializa Git em `main`, faz o commit `chore: estrutura inicial da Biblioteca Web`, cria o repositório GitHub e envia a base. DEV 2 clona o repositório. Não commitar dependências instaladas, artefatos de build ou credenciais pessoais.

```powershell
# DEV 1, após criar o esqueleto
git init -b main
git add .
git commit -m "chore: estrutura inicial da Biblioteca Web"
# Configurar origin com a URL real do repositório criado
git push -u origin main
git checkout -b feature/autores

# DEV 2, no próprio clone da base
git checkout -b feature/livros
```

### 3.3 Dependência entre as funcionalidades

Livro depende de `Autor`, `AutorRepository` e da listagem de autores no formulário. **Decisão técnica:** integrar primeiro o PR de Autores. DEV 2 pode preparar Livros em sua branch, mas só validar o conjunto quando a dependência estiver disponível. Após o merge de Autores:

```powershell
# DEV 2, com alterações locais já commitadas
git checkout main
git pull origin main
git checkout feature/livros
git merge main
```

Não recriar `Autor.java` na branch de Livros para contornar a dependência. Até os componentes de Livros existirem, `App.js` deve importar apenas componentes disponíveis; cada PR deve manter a base compilável. Resolver conscientemente os conflitos em `api.js` e `App.js`, preservando ambas as funcionalidades.

### 3.4 PR, revisão e merge obrigatórios

1. DEV 1 faz commits pequenos em `feature/autores`, envia a branch e abre PR para `main`. DEV 2 deixa **ao menos um comentário de revisão**, revisa e faz o merge.
2. DEV 2 incorpora a `main`, termina Livros e integração, envia `feature/livros` e abre PR para `main`. DEV 1 deixa **ao menos um comentário de revisão**, revisa e faz o merge.
3. Após os merges, ambos executam `git checkout main` e `git pull origin main`.
4. Preservar as duas branches no GitHub até a avaliação. **Decisão:** usar merge commit para tornar visível a integração; não apagar as branches automaticamente.

Exemplos de commits: `feat: Autor entity + repository`, `feat: AutorController (CRUD)`, `test: AutorControllerTest`, `feat: LivroForm com seleção de autor`.

O laboratório coloca Docker, CI e README após o passo dos PRs. Para seguir essa ordem sem fazer commits de implementação diretamente na `main`, complementar esses arquivos nas mesmas branches e abrir PRs adicionais com revisão cruzada. A exigência é **pelo menos dois PRs**, não exatamente dois. Depois de reutilizar uma branch já integrada, incorporar a `main` atual antes de novos commits.

Critério de aceite do processo: histórico real das duas branches, commits atribuíveis aos integrantes, pelo menos dois PRs para `main`, comentário de revisão do colega em cada um e merges concluídos. Código completo em um único commit na `main` não atende esse requisito. A opção de entregar ZIP não elimina a exigência do histórico e dos PRs no GitHub.

## 4. Estrutura de arquivos esperada

```text
biblioteca-web/
├── backend/
│   ├── pom.xml
│   ├── Dockerfile
│   └── src/
│       ├── main/
│       │   ├── java/com/biblioteca/
│       │   │   ├── BibliotecaApplication.java
│       │   │   ├── model/{Autor,Livro}.java
│       │   │   ├── repository/{AutorRepository,LivroRepository}.java
│       │   │   └── controller/{AutorController,LivroController}.java
│       │   └── resources/application.properties
│       └── test/
│           ├── java/com/biblioteca/
│           │   ├── controller/{AutorControllerTest,LivroControllerTest}.java
│           │   └── IntegracaoTest.java
│           └── resources/application-test.properties
├── frontend/
│   ├── package.json
│   ├── package-lock.json
│   ├── Dockerfile
│   ├── nginx.conf
│   ├── public/index.html
│   └── src/
│       ├── index.js
│       ├── App.js
│       ├── App.css
│       ├── services/api.js
│       └── components/
│           ├── AutorList.js
│           ├── AutorForm.js
│           ├── LivroList.js
│           └── LivroForm.js
├── .github/workflows/ci-cd.yml
├── .gitignore
├── docker-compose.yml
├── README.md
└── docs/
    ├── spec-implementacao-biblioteca-web.md
    └── evidencias/  # organização proposta para os prints
```

## 5. Modelo de dados obrigatório

### 5.1 Autor — tabela `autores`

| Atributo Java | Coluna | Tipo Java / SQL | Regras |
| --- | --- | --- | --- |
| `id` | `id` | `Long` / bigint | PK gerada automaticamente |
| `nome` | `nome` | `String` / varchar(100) | `@NotBlank`, `@Size(max=100)`, coluna não nula |
| `nacionalidade` | `nacionalidade` | `String` / varchar(50) | Opcional, `@Size(max=50)` |
| `dataNascimento` | `data_nascimento` | `LocalDate` / date | Opcional |
| `criadoEm` | `criado_em` | `LocalDateTime` / timestamp | Preenchimento automático na criação |

### 5.2 Livro — tabela `livros`

| Atributo Java | Coluna | Tipo Java / SQL | Regras |
| --- | --- | --- | --- |
| `id` | `id` | `Long` / bigint | PK gerada automaticamente |
| `titulo` | `titulo` | `String` / varchar(200) | `@NotBlank`, `@Size(max=200)`, coluna não nula |
| `isbn` | `isbn` | `String` / varchar(20) | Opcional, `@Size(max=20)` |
| `anoPublicacao` | `ano_publicacao` | `Integer` / integer | Opcional |
| `genero` | `genero` | `String` / varchar(50) | Opcional, `@Size(max=50)` |
| `autor` | `autor_id` | `Autor` / bigint FK | `@NotNull`, `@ManyToOne`, `@JoinColumn(name="autor_id", nullable=false)` |
| `criadoEm` | `criado_em` | `LocalDateTime` / timestamp | Preenchimento automático na criação |

Ambas usam `@Entity`, `@Table`, `@Id`, `@GeneratedValue` e Lombok `@Data`, conforme o modelo solicitado. Usar os imports `jakarta.persistence` e `jakarta.validation`, compatíveis com Spring Boot 3.

Relação unidirecional: `Autor (1) → Livro (N)`, com referência Java **somente em Livro**. Não adicionar `List<Livro>` em Autor. Todo livro tem exatamente um autor existente; um autor pode não ter livros. Não configurar remoção em cascata do autor nem exclusão automática de seus livros.

**Decisões técnicas:** usar geração `IDENTITY`, preencher `criadoEm` via `@PrePersist` e preservá-lo nos PUTs. Não impor ISBN único, formato de ISBN, ano positivo ou data de nascimento passada: o enunciado não estabelece essas restrições.

## 6. Persistência e configuração

- `AutorRepository extends JpaRepository<Autor, Long>`: `findAllByOrderByNomeAsc()`.
- `LivroRepository extends JpaRepository<Livro, Long>`: `findAllByOrderByTituloAsc()` e `findByAutorId(Long autorId)`.
- O método de busca por autor faz parte do passo obrigatório de repositórios, mesmo que expor a rota de filtragem seja extra.
- `application.properties`: PostgreSQL 15, banco `biblioteca_db`. Para o laboratório, o enunciado usa `postgres/postgres`.
- **Decisão técnica:** conexão local em `localhost:5432`, substituível por variável de ambiente no container; `spring.jpa.hibernate.ddl-auto=update` no desenvolvimento.
- `src/test/resources/application-test.properties`: H2 em memória, driver e dialeto correspondentes, criação/limpeza de schema para testes. Nenhum teste deve depender de PostgreSQL externo.
- Não usar `localhost` para acessar o banco de dentro do backend em container: o host interno é `postgres`.

## 7. Contrato REST obrigatório

Controllers com `@RestController`, `@RequestMapping`, `@CrossOrigin(origins="*")`, `@Valid` no corpo e respostas via `ResponseEntity`.

| Método | Endpoint | Sucesso | Recurso inexistente |
| --- | --- | --- | --- |
| POST | `/api/autores` | 201, autor criado | — |
| GET | `/api/autores` | 200, array em ordem de nome | — |
| GET | `/api/autores/{id}` | 200, autor | 404 |
| PUT | `/api/autores/{id}` | 200, autor atualizado | 404 |
| DELETE | `/api/autores/{id}` | **200** | 404 |
| POST | `/api/livros` | 201, livro criado com autor | Ver decisão sobre autor abaixo |
| GET | `/api/livros` | 200, array em ordem de título | — |
| GET | `/api/livros/{id}` | 200, livro | 404 |
| PUT | `/api/livros/{id}` | 200, livro atualizado, inclusive autor | 404 |
| DELETE | `/api/livros/{id}` | **200** | 404 |

Manter DELETE com 200, conforme a tabela do professor. Listas vazias retornam `[]` com 200. JSON usa camelCase; datas usam `AAAA-MM-DD` e timestamps seguem ISO 8601.

Exemplo de criação de autor:

```json
{"nome":"Machado de Assis","nacionalidade":"Brasileira","dataNascimento":"1839-06-21"}
```

Exemplo de criação de livro, usando o ID realmente retornado pela criação do autor:

```json
{"titulo":"Dom Casmurro","isbn":"978-8535910667","anoPublicacao":1899,"genero":"Romance","autor":{"id":1}}
```

**Decisões técnicas para completar o contrato:**

- POST e PUT de Livro buscam o autor por `autor.id` antes de salvar e associam a entidade persistida. Um ID de autor inexistente retorna 404; objeto `autor` ausente ou sem ID retorna 400. `@NotNull` no objeto sozinho não valida seu ID.
- Violações dos campos obrigatórios ou de seus limites retornam 400. O cliente não define os IDs gerados nem o timestamp de criação.
- PUT carrega o registro pelo ID da URL e atualiza seus campos editáveis. No Livro, atualiza também `autor`; campos opcionais podem ser limpos.
- A resposta de Livro deve permitir à interface obter `autor.id` e `autor.nome`; usar a entidade de autor resolvida, sem referência reversa a livros.
- Autor com livros permanece protegido pela FK. O fluxo obrigatório remove primeiro os livros. Tratar a violação com mensagem amigável e 409 é **extra**.

Swagger é apenas uma alternativa de ferramenta para testar a API; não é uma dependência obrigatória. Usar curl ou Postman dispensa sua instalação.

## 8. Testes e critérios de aceite

### 8.1 Obrigatórios

| Arquivo | Configuração | Casos mínimos |
| --- | --- | --- |
| `AutorControllerTest.java` | `@WebMvcTest`, `@MockBean`, Mockito e MockMvc | Criação válida retorna 201; busca de ID inexistente retorna 404 |
| `LivroControllerTest.java` | `@WebMvcTest`, `@MockBean`, Mockito e MockMvc | Criação válida com autor retorna 201; busca de ID inexistente retorna 404 |
| `IntegracaoTest.java` | `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")` | `deveVincularLivroAAutor`: POST de autor, leitura do ID, POST do livro com esse ID e verificação do vínculo |

São **pelo menos quatro testes de controller e um de integração**. No teste unitário de Livro, mockar também `AutorRepository` se o controller o utilizar. Na integração, usar os repositories reais e H2; não mockar a persistência. Isolar os dados para não depender da ordem dos testes.

**Decisão de verificação do vínculo:** após o POST de Livro, conferir o ID do autor retornado e buscar o livro criado para verificar que o relacionamento foi persistido.

Executar em `backend/`:

```powershell
mvn test -Dtest="*ControllerTest"
mvn test -Dtest="IntegracaoTest"
mvn test
```

Capturar o `BUILD SUCCESS` dos testes de controllers e da suíte completa. Conferir que houve testes executados e nenhum erro ou falha. Não apresentar apenas um build sem testes como evidência.

### 8.2 Verificação funcional da implementação

Mesmo sem ampliar a suíte mínima, conferir os dez endpoints, as duas ordenações, os status de sucesso/404, a validação dos campos e a troca de autor no PUT. Validar os formulários de criação e edição, as exclusões, o nome do autor na tabela de livros e a persistência do vínculo. Fazer isso antes de registrar as evidências finais.

## 9. Frontend obrigatório

`services/api.js` expõe `autorService` e `livroService`, cada um com `listar`, `buscar`, `criar`, `atualizar` e `deletar`. Centralizar Axios e a URL base da API.

| Componente | Comportamento |
| --- | --- |
| `AutorList.js` | Tabela de autores, ação de novo autor, botões Editar e Excluir |
| `AutorForm.js` | Mesmo formulário para novo/editar; nome obrigatório; nacionalidade e nascimento opcionais |
| `LivroList.js` | Tabela de livros mostrando o **nome do autor**, botões Editar/Excluir e acesso a novo livro |
| `LivroForm.js` | Novo/editar; título obrigatório, campos opcionais e `<select>` obrigatório de autores carregado pela API |

`App.js` deve ter menu e estas seis rotas:

```text
/autores
/autores/novo
/autores/editar/:id
/livros
/livros/novo
/livros/editar/:id
```

**Decisões de interface:** rota `/` redireciona para `/autores`; ao editar, carregar os dados atuais; o select envia `autor: {id: Number(valorSelecionado)}`. Se não houver autores, explicar que é necessário cadastrar um e impedir o envio do livro. Converter ano preenchido para número e opcionais vazios para valores compatíveis com o backend, especialmente datas e inteiros. Exibir falhas da API e atualizar a listagem após salvar/excluir.

Criar `App.css` se importado, garantir `public/index.html` e manter React Router 6. Desenvolvimento via `npm start` em `http://localhost:3000`. A API fica em `http://localhost:8080/api`.

## 10. Docker e Compose

**Obrigatório:** `backend/Dockerfile`, `frontend/Dockerfile`, `frontend/nginx.conf` e `docker-compose.yml` com os três serviços abaixo.

| Serviço | Porta publicada | Configuração |
| --- | --- | --- |
| `postgres` | `5432:5432` | Imagem PostgreSQL 15, banco `biblioteca_db`, usuário/senha de laboratório |
| `backend` | `8080:8080` | Build Maven/Java 17 e JDBC apontando para `postgres:5432/biblioteca_db` |
| `frontend` | `3000:80` | Build React com Node 20 e arquivos estáticos servidos por Nginx |

**Decisões técnicas:** Dockerfiles em múltiplos estágios; volume para dados do PostgreSQL; healthcheck do banco e dependência do backend em banco saudável. No Nginx, configurar fallback para `index.html` para permitir abrir diretamente as rotas React. Usar `/api` no frontend em produção com proxy Nginx para `http://backend:8080`, preservando o caminho; em desenvolvimento, Axios usa a API local. O nome `backend` é resolvido entre containers, não pelo navegador do usuário.

Na raiz, `docker compose up -d postgres` prepara o banco para desenvolvimento. `docker compose up -d --build` sobe o conjunto. A grafia `docker-compose` do enunciado é equivalente quando o executável legado estiver instalado; documentar a variante disponível.

Executar o conjunto e fazer aceitação com curl é **extra no enunciado**. Os arquivos, as portas e a configuração coerente dos três serviços continuam obrigatórios; o README deve explicar a execução.

## 11. GitHub Actions obrigatório

Arquivo: `.github/workflows/ci-cd.yml`.

- Disparos: `push` na `main` e `pull_request` com destino à `main`.
- Job **`test-backend`**: checkout, Java 17 e `mvn test` dentro de `backend/`; a suíte inclui controllers e `IntegracaoTest` com H2.
- Job **`test-frontend`**: checkout, Node 20 e, dentro de `frontend/`, `npm ci`, `npm test -- --watchAll=false --passWithNoTests` e `npm run build`.
- Versionar `package-lock.json` sincronizado com `package.json`, pois `npm ci` depende dele.
- Corrigir falhas até obter execução verde na `main` final e capturar a aba Actions. Um YAML apenas presente no repositório não comprova a execução pedida.

**Decisão de processo:** na fase final, introduzir o workflow por PR e confirmar sua execução tanto nesse PR quanto após o merge. PRs anteriores à existência do workflow precisam dos testes locais. Depois que o CI existir, concluir merges somente com os checks verdes.

Job de deploy com build/aceitação é extra. Deploy AWS ECR/ECS, RDS, ALB e conta/credenciais AWS não são exigidos. JaCoCo pode ser acrescentado como melhoria, sem meta de cobertura imposta por esta spec.

## 12. Sequência de implementação e responsáveis

A ordem abaixo corresponde aos 13 passos do laboratório. Configurações podem começar mínimas no esqueleto e ser completadas no passo correspondente.

| Passo | Obrigação | Responsável / branch | Conclusão verificável |
| --- | --- | --- | --- |
| 1 | Repositório e estrutura | DEV 1, base inicial em `main`; DEV 2 clona | Commit inicial, GitHub, pastas, manifests e `.gitignore` |
| 2 | Branches | Cada DEV na sua branch | `feature/autores` e `feature/livros` criadas a partir da base |
| 3 | PostgreSQL e H2 | DEV 1 em `feature/autores`; DEV 2 incorpora ao integrar | Banco `biblioteca_db`, perfil `test` independente |
| 4 | Entidades | Cada DEV na sua branch | Campos, validações e FK unidirecional corretos |
| 5 | Repositories | Cada DEV na sua branch | CRUD JPA, ordenações e método de busca por autor |
| 6 | Controllers | Cada DEV na sua branch | Dez endpoints e status previstos |
| 7 | Testes unitários | Cada DEV na sua branch | Dois testes mínimos por controller e print de sucesso |
| 8 | Integração | DEV 2 em `feature/livros` | Vínculo Autor–Livro persistido em H2 |
| 9 | Frontend | Cada DEV na sua branch; coordenar arquivos comuns | Quatro componentes, serviços, menu e seis rotas |
| 10 | Push, PR, revisão, merge | Autores primeiro, Livros após incorporar a base | Dois PRs com comentário do colega e merge; ambos atualizam `main` |
| 11 | Docker/Compose | Complementos nas branches e PR adicional | Dois Dockerfiles, Nginx e três serviços configurados |
| 12 | CI | DEV 2 em `feature/livros`, PR revisado por DEV 1 | Dois jobs verdes na versão final |
| 13 | README | DEV 1 em `feature/autores`, PR revisado por DEV 2 | Documentação completa, evidências e instruções reproduzíveis |

Os passos das funcionalidades são executados por cada integrante em sua trilha. Como Livros depende de Autores, DEV 1 pode concluir sua trilha e integrar o primeiro PR enquanto DEV 2 prepara seus arquivos. A validação integrada de Livros ocorre após incorporar Autores.

Planejar o tempo do laboratório priorizando todos os obrigatórios, reservando o fim para revisão, merges, execução do CI e coleta dos prints. Não deixar GitHub, revisão e evidências para depois do prazo.

## 13. README e pacote de entrega

### 13.1 Conteúdo obrigatório do README

- Nome e descrição do sistema.
- Tabela de tecnologias e versões e estrutura de pastas.
- Como executar com Docker Compose, incluindo portas e banco.
- Como rodar os testes backend e frontend.
- Divisão real da dupla, nomes e responsabilidades.

**Complementos propostos:** turma, pré-requisitos, execução local (`mvn spring-boot:run` e `npm start`), modelo Autor–Livro, exemplo JSON, links dos PRs e das evidências, nota sobre exclusão de autor com livros. O README final deve refletir comandos efetivamente verificados.

### 13.2 Evidências obrigatórias

Organização proposta em `docs/evidencias/`:

| Evidência | Conteúdo mínimo |
| --- | --- |
| `01-testes-controllers.png` | Comando dos testes unitários e `BUILD SUCCESS` |
| `02-testes-completos.png` | `mvn test`, incluindo integração, com `BUILD SUCCESS` |
| `03-autores-lista.png` | Tela da lista de Autores |
| `04-autores-formulario.png` | Formulário de Autores |
| `05-livros-lista.png` | Tela da lista de Livros com nome do autor |
| `06-livros-formulario.png` | Formulário de Livros com o select de autores visível |
| `07-api-vinculo.png` | Teste curl/Postman/Swagger: POST de livro com ID de autor válido e resposta de criação |
| `08-actions.png` | Aba Actions com execução bem-sucedida do CI da versão final |

Os nomes e a pasta são decisões de organização; os conteúdos dos prints vêm do enunciado. Não simular prints nem marcar evidências ausentes como concluídas.

Entregar o link do repositório GitHub, público ou com o professor como colaborador, ou ZIP do projeto sem `node_modules/` e `target/`. Em ambos os casos, disponibilizar o histórico das branches e dos PRs no GitHub para avaliação. Manter no repositório Compose, Dockerfiles, Nginx, workflow, README e lockfile.

## 14. Extras explicitamente separados

Somente após concluir os obrigatórios:

1. `GET /api/livros/autor/{autorId}` retornando 200 e os livros do autor.
2. Testes de listagem ordenada.
3. `deveExecutarFluxoCompletoAutor`: criar → buscar → atualizar → excluir.
4. Subir todo o Compose e executar aceitação com curl, criando autor e livro vinculado.
5. Job adicional de deploy/build de containers e testes de aceitação.
6. Resposta amigável 409 ao excluir autor com livros.

Extras podem render bônus a critério do professor; o enunciado exemplifica até +0,3. Não condicionar a conclusão dos obrigatórios a essas melhorias.

## 15. Rastreabilidade da avaliação e checklist final

| Critério | Pontos | Cobertura nesta spec | Evidência de conclusão |
| --- | --- | --- | --- |
| Git/GitHub | 0,45 | Seções 3 e 12 | Duas branches, commits, pelo menos dois PRs revisados e integrados |
| Modelagem | 0,45 | Seções 5–6 | Entidades e FK corretas, sem coleção reversa em Autor |
| Backend Autores | 0,45 | Seções 6–8 | Cinco endpoints, ordenação e status corretos |
| Backend Livros | 0,45 | Seções 6–8 | Cinco endpoints, ordenação e vínculo no POST/PUT |
| Testes | 0,45 | Seção 8 | Testes mínimos e integração passando; prints |
| Frontend React | 0,45 | Seção 9 | Serviços, quatro componentes, rotas e select de autor |
| Docker + CI + README | 0,30 | Seções 10–13 | Arquivos completos, Actions executado e documentação |
| **Total** | **3,00** | | |

- [ ] Stack e versões obrigatórias respeitadas; lockfile versionado.
- [ ] DEV 1 desenvolveu Autores em `feature/autores` e DEV 2 desenvolveu Livros em `feature/livros`.
- [ ] Pelo menos dois PRs com revisão/comentário do colega e merge para `main`; branches preservadas.
- [ ] Ambos atualizaram sua `main` e compreendem as duas funcionalidades.
- [ ] Todos os campos, tamanhos, nomes das colunas e preenchimento automático implementados.
- [ ] Relação obrigatória em Livro, FK `autor_id`, sem `List<Livro>` em Autor.
- [ ] Dez endpoints implementados; 201/200/404 corretos; listagens ordenadas.
- [ ] PUT de Livro altera também o autor; exclusão não viola a integridade dos dados.
- [ ] Quatro testes mínimos de controllers e `deveVincularLivroAAutor` passando com H2.
- [ ] Quatro componentes, seis rotas, menu, serviços Axios e select de autor funcionando.
- [ ] Dois Dockerfiles, Nginx e Compose com PostgreSQL 15, backend e frontend nas portas exigidas.
- [ ] Workflow com `test-backend` e `test-frontend`, push/PR para `main` e execução final verde.
- [ ] README com execução, testes, tecnologias, estrutura e divisão real da dupla.
- [ ] Prints de testes, quatro telas, vínculo pela API e Actions coletados.
- [ ] Repositório acessível ao professor ou ZIP preparado sem dependências/builds; histórico e PRs acessíveis.
- [ ] Entrega enviada pelo canal indicado até o fim da aula.

Esta checklist descreve a conclusão futura. A criação desta spec não representa execução da aplicação, dos testes, dos PRs ou do CI.
