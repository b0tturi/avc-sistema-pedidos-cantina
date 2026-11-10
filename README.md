Integrantes:
   Júlio César Gualter de Lima RA: 5487153 
   Erick Hilquias Botturi Negrão RA: 5476739
   Eric Varjão Mello RA: 5495202 
   Vinicius Loureiro RA: 5487021 
   Vinicius Augusto Meneses Macena RA: 5515190 
   Henrique Tadeu Fazio Campos Maraco RA: 5552141
   Lucas Diaz Moulaz RA: 5380278
   Henrique Diaz Vieira RA: 5384354
   Pedro Henrique Ferraresi RA: 5416990
   Kenyi Manuel Huamani Zirena RA: 5242304

# Sistema de Pedidos de Cantina — Tema 4

API REST desenvolvida em **Java 21**, **Spring Boot**, **Spring Data JPA** e **PostgreSQL**, como parte do projeto acadêmico Tema 4 — Sistema de Pedidos de Cantina.

## Requisitos

- JDK 21
- PostgreSQL
- Gradle Wrapper incluído no projeto

## Banco de dados

1. Crie um banco de dados chamado `avc_cantina` no PostgreSQL.
2. Execute o arquivo `database/schema.sql` conectado a esse banco.
3. Configure as variáveis de ambiente conforme o arquivo `.env.example`:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`

A aplicação utiliza `spring.jpa.hibernate.ddl-auto=none`. Portanto, as tabelas são criadas pelo script SQL, e não automaticamente pelo Hibernate.

## Executar a API

No Windows, abra o PowerShell na pasta raiz do projeto e execute:

```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/avc_cantina"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="sua_senha"
.\gradlew.bat bootRun
```

A API inicia, por padrão, em:

`http://localhost:8080`

## Endpoints

| Método | Rota | Descrição |
|---|---|---|
| GET | `/api/clientes` | Lista clientes |
| GET | `/api/clientes/{id}` | Busca cliente por ID |
| POST | `/api/clientes` | Cadastra cliente |
| GET | `/api/produtos` | Lista produtos |
| GET | `/api/produtos/{id}` | Busca produto por ID |
| POST | `/api/produtos` | Cadastra produto |
| GET | `/api/pedidos` | Lista pedidos |
| GET | `/api/pedidos?situacao=ATIVO` | Filtra pedidos por situação (`ATIVO` ou `CANCELADO`) |
| POST | `/api/pedidos` | Registra um pedido de uma unidade |
| PATCH | `/api/pedidos/{id}/cancelamento` | Cancela um pedido e devolve a unidade ao estoque |

### Exemplo de corpo para criar pedido

```json
{
  "clienteId": 1,
  "produtoId": 1
}
```

## Regras de negócio

- Cada pedido representa uma unidade de um produto.
- O pedido registra o preço vigente no momento da compra.
- O estoque é decrementado ao criar o pedido.
- Não é permitido comprar produtos sem estoque.
- O cancelamento devolve uma unidade ao estoque.
- Um pedido cancelado não pode ser cancelado novamente.
- As operações de estoque utilizam transações e bloqueio pessimista do registro do produto, evitando conflitos em compras simultâneas da última unidade.

## Testes

Para executar os testes, abra o PowerShell na pasta raiz do projeto e execute:

```powershell
.\gradlew.bat test
```

Os testes do `PedidoService` cobrem criação de pedidos, falta de estoque, cancelamento, tentativa de cancelamento duplicado e situações inválidas.

Os testes utilizam mocks e não necessitam de uma conexão com o PostgreSQL.

## Front-end

O projeto também possui uma interface web desenvolvida com React e Vite, localizada na pasta `frontend`.

Para executá-la, abra outro terminal PowerShell:

```powershell
cd frontend
npm.cmd install
npm.cmd run dev
```

Por padrão, o front-end fica disponível em:

`http://localhost:5173`

## CORS

Durante o desenvolvimento, a API aceita chamadas dos endereços `http://localhost:5173` e `http://localhost:3000`.

Caso o front-end seja executado em outro endereço, ajuste as configurações de CORS na classe `WebConfig`.

## Uso de inteligência artificial

Durante o desenvolvimento deste projeto, foram utilizadas ferramentas de inteligência artificial como apoio à implementação, revisão e correção de código, organização da documentação e resolução de problemas técnicos.

O uso dessas ferramentas ocorreu como recurso auxiliar ao processo de desenvolvimento e aprendizagem, não substituindo a análise, os testes e a validação do projeto.

## Decisões Técnicas Essenciais

- **JDBC puro com `DataSourceUtils`:** A migração de JPA/Hibernate para JDBC puro exigiu manter o controle transacional do Spring (`@Transactional`). O uso de `DataSourceUtils.getConnection(dataSource)` recupera a mesma conexão física já vinculada à transação da thread em execução. Isso garante operações atômicas (commit/rollback automáticos gerenciados pelo Spring) e evita vazamento de conexões (*connection leaks*).
- **Bloqueio Pessimista (`FOR UPDATE`):** Para o controle de estoque concorrente, a verificação e decremento do produto utilizam a cláusula SQL `SELECT ... FOR UPDATE`. Ela aplica um lock exclusivo na linha do registro no PostgreSQL até o encerramento da transação, prevenindo condições de corrida (*race conditions*) caso requisições paralelas comprem a última unidade ao mesmo tempo.
- **HTTP 409 (Conflict) para Falta de Estoque e Cancelamento Duplicado:** Comprar um produto com estoque zerado ou tentar cancelar um pedido que já está cancelado constitui uma violação de estado atual do recurso no servidor, e não um erro de sintaxe/formatação da requisição (400 Bad Request) nem falha de infraestrutura (500). O status `409 Conflict` comunica formalmente essa incompatibilidade com o estado do banco.

---

## Análise de Algoritmos (Busca e Ordenação)

- **Busca Linear — $O(n)$:** Examina os elementos sequencialmente até encontrar a chave ou percorrer toda a lista. Sua complexidade no pior caso é linear, $O(n)$. É ideal para coleções pequenas ou não ordenadas, onde o custo de ordenar previamente não compensa.
- **Busca Binária — $O(\log n)$:** Reduz a área de pesquisa pela metade a cada passo, apresentando complexidade logarítmica, $O(\log n)$. Requer **obrigatoriamente** que a coleção esteja previamente ordenada pela mesma chave utilizada na busca (neste projeto, o atributo identificador `id`).
- **Algoritmo de Ordenação Escolhido (Timsort):**
  - Implementação nativa do Java via `Collections.sort()` / `List.sort()`.
  - **Complexidade:** $O(n)$ no melhor caso (dados já ordenados) e $O(n \log n)$ no caso médio e pior caso.
  - **Justificativa:** É um algoritmo híbrido (Merge Sort + Insertion Sort), estável e de alta performance na memória para garantir a pré-condição exigida pela Busca Binária.

---

## Evidências de Testes

Os testes automatizados foram executados via Gradle Wrapper:

```powershell
.\gradlew.bat test
