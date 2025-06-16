# Autenticador de CVV

Objetivo

O Autenticador de CVV é um aplicativo desktop desenvolvido em Java com interface gráfica Swing. Ele gera códigos de segurança (semelhantes ao CVV de cartões), valida entradas do usuário e armazena os códigos gerados em um banco de dados SQLite local. O sistema foi desenvolvido para evitar que pessoas sofram golpes relacionados a cartões de crédito virtuais, com essa interface, o código de segurança presente no cartão físico se torna mais protegido, pois é preciso verificá-lo após fazer qualquer compra.

---

Tecnologias Utilizadas

- Java SE
- Swing (GUI)
- FlatLaf (visual moderno)
- SQLite (banco de dados local)
- JDBC

---

Como Executar

1. Requisitos
- JDK 17 ou superior instalado.
- IDE como IntelliJ, Eclipse ou compilador de terminal.
- Biblioteca FlatLaf no classpath (pode ser baixada via [Maven Central](https://mvnrepository.com/artifact/com.formdev/flatlaf)).
- Versão do FlatLaf usada: 3.6
- Versão do SQLite JDBC usada: 3.49.1.0

2. Compilar
Compile todos os arquivos `.java`, incluindo `ProjetoA3.java`, `BotaoInterativo`, `PainelTemporizador`, etc.

Exemplo no terminal:


javac -cp ".;flatlaf-<versao>.jar" ProjetoA3.java


3. Executar

bash
java -cp ".;flatlaf-<versao>.jar;sqlite-jdbc-<versao>.jar" ProjetoA3




Exemplo de Uso

Ao abrir o sistema, o usuário verá:

- Um **código de 3 dígitos** gerado automaticamente.
- Um campo para digitar e validar o código.
- Um **temporizador visual em anel** regressivo de 60 segundos.
- Se o código estiver correto: exibe **"Acesso Liberado!"** em verde.
- Se incorreto: exibe **"Acesso Negado! Código alterado."** em vermelho.
- Um botão para **"Tentar Novamente"** e outro para **abrir o banco de dados SQLite**.


---

Banco de Dados

- O sistema utiliza um banco SQLite chamado `autenticador.db`.
- Todos os códigos gerados são salvos, com limite de 100 registros mais recentes.

---

Desenvolvedores

- Pedro Trofino
- Matheus Chervenhak
- João Dias
- Pedro Portela
