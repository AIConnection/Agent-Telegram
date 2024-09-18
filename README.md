# **Documento de Projeto: Agente Virtual Inteligente com Reconhecimento de Voz e Integração com LLM**

## **1. Introdução**

Este documento descreve o desenvolvimento de um agente virtual inteligente que interage com usuários via **Telegram**, utilizando **Spring Boot**, **Spring AI** e a biblioteca **telegrambots-abilities**. O agente é capaz de processar mensagens de texto e de voz, utilizando a **API da Groq** para converter áudio em texto e um **Large Language Model (LLM)** para gerar respostas contextuais. A arquitetura foi projetada com foco em modularidade, escalabilidade e aderência aos princípios de **Engenharia de Software**.

---

## **2. Requisitos do Projeto**

### **2.1. Funcionais**
- O sistema deve ser capaz de receber e processar mensagens de texto enviadas pelos usuários no Telegram.
- O sistema deve converter mensagens de voz em texto e processar a transcrição.
- O sistema deve integrar-se com um **Large Language Model (LLM)** para gerar respostas inteligentes baseadas nas mensagens dos usuários.
- O sistema deve retornar a resposta gerada pelo **LLM** ao usuário no Telegram, seja em resposta a mensagens de texto ou voz.

### **2.2. Não Funcionais**
- O sistema deve ser modular e escalável.
- Deve garantir tempo de resposta adequado para interações em tempo real, considerando a integração com serviços externos.
- A arquitetura deve facilitar substituições de serviços externos (como a API de reconhecimento de voz) sem grandes modificações.
- A modularidade do sistema deve permitir fácil manutenção e testes unitários.

---

## **3. Arquitetura do Sistema**

### **3.1. Arquitetura Geral**

O sistema segue uma arquitetura composta por três componentes principais:

1. **Cliente (Telegram)**: O usuário interage com o agente através do Telegram, enviando mensagens de texto ou de voz.

2. **Servidor Backend (Spring Boot)**: O backend recebe as mensagens do Telegram e processa as interações. Ele contém vários módulos independentes que lidam com diferentes responsabilidades (transcrição de voz, integração com LLM, etc.).

3. **Serviços Externos**: O sistema integra-se a serviços externos como a **API da Groq** para transcrição de voz e o **Spring AI** para gerar respostas baseadas em linguagem natural.

### **3.2. Componentes**

- **PlnAgent**: Responsável por interagir diretamente com o Telegram, utilizando a biblioteca **telegrambots-abilities**. Define as habilidades do bot, incluindo comandos de texto e processamento de mensagens de voz.

- **VoiceRecognitionService**: Integra-se com a **API da Groq** para transcrever áudios em formato MP3 para texto.

- **AudioConversionService**: Converte arquivos de áudio de OGG (formato padrão do Telegram) para MP3, permitindo o processamento do áudio.

- **Spring AI Integration**: Módulo que realiza a comunicação com o **LLM** para processar entradas de texto ou transcrições e gerar respostas inteligentes.

---

## **4. Conceitos Aplicados**

### **4.1. Processamento de Linguagem Natural (PLN)**

O sistema faz uso de **Inteligência Artificial** por meio do **Processamento de Linguagem Natural (PLN)**, utilizando um **Large Language Model (LLM)** para interpretar as mensagens dos usuários e gerar respostas. Modelos como o GPT são utilizados para entender o contexto das mensagens e fornecer respostas em linguagem natural, simulando uma interação humana fluida.

### **4.2. Reconhecimento de Fala**

O sistema utiliza a **API da Groq** para converter mensagens de voz em texto, permitindo ao bot processar áudios enviados pelos usuários. A API da Groq transcreve o áudio em formato MP3 para texto, que posteriormente é processado pelo LLM.

### **4.3. Conversão de Áudio**

Mensagens de voz enviadas pelo Telegram estão no formato OGG, incompatível com muitos serviços de transcrição. O **AudioConversionService** resolve isso convertendo o arquivo OGG para MP3, um formato aceito pela API da Groq para transcrição de voz.

### **4.4. Gerenciamento de Arquivos Temporários**

Para manipular os arquivos de áudio durante a conversão e transcrição, o sistema utiliza práticas eficientes de gerenciamento de arquivos temporários, garantindo que os recursos sejam liberados após o uso. Isso inclui a utilização de fluxos de dados em memória quando possível e a limpeza automática de arquivos temporários, melhorando a eficiência e a segurança da aplicação.

---

## **5. Frameworks Utilizados**

### **5.1. Spring Boot**
O **Spring Boot** é o framework principal utilizado no desenvolvimento do backend. Ele oferece um ambiente robusto para a construção de aplicações web e APIs, com fácil integração de serviços e alta escalabilidade.

### **5.2. Spring AI**
O **Spring AI** é utilizado para integrar o sistema com **Large Language Models (LLMs)**. Ele facilita a comunicação com serviços como o GPT da OpenAI, permitindo que o agente processe entradas de texto e gere respostas inteligentes com base no contexto.

### **5.3. Telegram Bots API e telegrambots-abilities**
A biblioteca **telegrambots-abilities** abstrai grande parte da complexidade da API do Telegram, permitindo que o desenvolvedor defina habilidades de forma modular. Isso facilita o desenvolvimento de bots interativos, como o suporte a comandos de texto e o processamento de mensagens de voz.

### **5.4. Bibliotecas de Conversão de Áudio**
Bibliotecas como o **FFmpeg** são utilizadas para a conversão de arquivos de áudio. O sistema converte arquivos de OGG, formato padrão das mensagens de voz no Telegram, para MP3, que é aceito pela API da Groq.

---

## **6. Serviços Utilizados**

### **6.1. API da Groq**
A **API da Groq** é responsável pela transcrição de voz no projeto, convertendo arquivos de áudio MP3 em texto. Este texto é então processado pelo **LLM** para gerar respostas inteligentes.

#### **Como Configurar a API da Groq**
1. Acesse o [Groq Console](https://console.groq.com) e crie uma conta.
2. Gere uma chave de API na seção **API Keys**.
3. Configure a chave no arquivo `application.properties` ou no sistema de gerenciamento de configurações:
   ```properties
   spring.ai.openai.api-key=YOUR_GROQ_API_KEY
   ```

### **6.2. OpenAI GPT via Spring AI**
O **Spring AI** integra o sistema com um **LLM**, permitindo que o bot interprete e responda a mensagens com base em linguagem natural. O **LLM** é configurado para processar mensagens de texto ou voz (convertidas em texto).

### **6.3. Configuração do Bot do Telegram**

Para que o agente virtual possa interagir com os usuários via Telegram, é necessário criar um bot, obter as chaves de acesso (tokens) e utilizar o SDK do Telegram. A seguir, são detalhados os passos necessários.

#### **6.3.1. Criação do Bot no Telegram**

1. **Iniciar Conversa com o BotFather**:
   - No aplicativo do Telegram, pesquise por **@BotFather** e inicie uma conversa.
2. **Criar um Novo Bot**:
   - Envie o comando `/newbot` ao BotFather.
   - Siga as instruções fornecendo um nome para o seu bot (por exemplo, "Agente Virtual Inteligente").
   - Escolha um nome de usuário para o bot que termine com `bot` (por exemplo, "AgenteVirtualBot").
3. **Obter o Token de Acesso**:
   - Após a criação, o BotFather fornecerá um token de API no formato:
     ```
     123456789:ABCdefGHIjklMNO_pqrSTUvwxYZ
     ```
   - Guarde este token com segurança; ele será usado para configurar o acesso do seu aplicativo ao bot.

#### **6.3.2. Configuração do Token no Aplicativo**

- No arquivo `application.properties` ou no sistema de gerenciamento de configurações, adicione o token do bot:
  ```properties
  botToken=<YOUR_TELEGRAM_API_KEY>
  ```

#### **6.3.3. Utilização do SDK do Telegram**

- **Dependência Maven**:
  - Certifique-se de adicionar a dependência do **telegrambots-abilities** no seu `pom.xml`:
    ```xml
    <dependency>
        <groupId>org.telegram</groupId>
        <artifactId>telegrambots-abilities</artifactId>
        <version>5.5.0</version>
    </dependency>
    ```

- **Implementação do Bot**:
  - Crie uma classe que estenda `AbilityBot` fornecida pela biblioteca `telegrambots-abilities`.
  - Implemente os métodos necessários, como `onUpdateReceived`, para processar as mensagens recebidas.

- **Registro do Bot**:
  - No seu aplicativo Spring Boot, registre o bot como um bean para que ele seja inicializado corretamente:
    ```java
    @Bean
    public PlnAgent telegramBot() {
        return new PlnAgent(telegramBotToken, telegramBotUsername);
    }
    ```

- **Inicialização**:
  - Ao iniciar o aplicativo, o bot estará registrado e pronto para receber mensagens dos usuários.

#### **6.3.4. Teste do Bot**

- Envie uma mensagem para o seu bot no Telegram para verificar se ele está respondendo conforme o esperado.
- Certifique-se de que o bot está online e que o aplicativo está em execução.

---

## **7. Engenharia de Software Aplicada**

### **7.1. Princípios SOLID**
Os princípios **SOLID** foram aplicados durante o desenvolvimento do projeto para garantir modularidade e manutenibilidade, facilitando a escalabilidade e a extensibilidade do sistema.

- **Single Responsibility Principle (SRP)**: Cada classe ou módulo tem uma única responsabilidade. Por exemplo, o `AudioConversionService` é responsável apenas pela conversão de arquivos de áudio.

- **Open/Closed Principle (OCP)**: O código é aberto para extensão, permitindo a adição de novos módulos sem necessidade de modificar o código existente. Novas funcionalidades, como suporte para outros formatos de áudio ou integração com novos serviços de LLM, podem ser facilmente adicionadas.

- **Liskov Substitution Principle (LSP)**: O design permite que subclasses ou implementações de interfaces substituam suas classes base sem afetar o comportamento geral do sistema.

- **Interface Segregation Principle (ISP)**: Interfaces foram desenhadas de forma que as classes dependam apenas dos métodos necessários para suas funcionalidades específicas, evitando dependências de funcionalidades não utilizadas.

- **Dependency Inversion Principle (DIP)**: A injeção de dependências foi aplicada para desacoplar a lógica de negócio das implementações concretas. Isso facilita a substituição de serviços, como a API de reconhecimento de fala, e melhora a testabilidade.

### **7.2. Arquitetura em Camadas**
O projeto adota uma arquitetura em camadas, separando claramente as responsabilidades:

- **Camada de Apresentação**: Responsável pela interação com o Telegram.
- **Camada de Lógica de Negócio**: Onde ocorre o processamento das mensagens e a integração com serviços externos.
- **Camada de Infraestrutura**: Gerencia operações como conversão de áudio e transcrição de voz.

### **7.3. Modularidade e Testabilidade**
A modularidade do sistema permite a substituição de componentes sem afetar o funcionamento global. Por exemplo, o serviço de transcrição de voz pode ser substituído por outro serviço de reconhecimento de fala sem impactar o restante do sistema. Além disso, o uso de **injeção de dependência** facilita a criação de testes unitários, permitindo que serviços externos sejam simulados para testes em ambiente controlado.

### **7.4. Gerenciamento Eficiente de Arquivos Temporários**

O sistema utiliza práticas recomendadas para o gerenciamento de arquivos temporários, garantindo que os recursos sejam liberados adequadamente após o uso. Isso inclui a utilização de fluxos de dados em memória quando possível e a limpeza automática de arquivos temporários, melhorando a eficiência e a segurança da aplicação.

---

## **8. Testes e Validação**

### **8.1. Testes Unitários**
O projeto foi desenvolvido com modularidade, facilitando a criação de testes unitários. A abordagem de injeção de dependência permite a utilização de simulações (mocks) para serviços externos, como a **API da Groq** e a **Spring AI**, possibilitando a verificação do comportamento do sistema sem necessidade de dependências externas durante os testes.

Exemplos de módulos testáveis incluem:

- **VoiceRecognitionService**: Testes para validar se a transcrição de áudio está ocorrendo conforme o esperado.
- **AudioConversionService**: Testes para verificar a integridade da conversão de arquivos de áudio.

### **8.2. Testes de Integração**
Os testes de integração verificam se os componentes do sistema estão se comunicando corretamente, garantindo que o fluxo de dados entre o bot do Telegram, a **API da Groq** e a **Spring AI** funcione como esperado. Esses testes simulam interações reais do usuário, como o envio de mensagens de voz, sua transcrição e a resposta gerada pelo LLM.

---

## **9. Conclusão**

Este projeto apresenta uma solução completa para a construção de um agente virtual inteligente, utilizando tecnologias modernas para permitir a interação com usuários por meio do Telegram. A arquitetura modular, aliada aos princípios de engenharia de software, garante que o sistema seja flexível, escalável e fácil de manter. A integração com serviços como a **API da Groq** e o **Spring AI** exemplifica como plataformas externas podem ser utilizadas para resolver problemas complexos, como reconhecimento de fala e geração de respostas inteligentes.

O gerenciamento eficiente de arquivos temporários e o respeito aos princípios **SOLID** reforçam a robustez e a eficiência da solução.

---

### **Referências**
- **Spring AI Documentation**: [https://spring.io/projects/spring-ai](https://spring.io/projects/spring-ai)
- **Groq Speech-to-Text API**: [https://console.groq.com/docs/speech-text](https://console.groq.com/docs/speech-text)
- **Telegram Bots API Documentation**: [https://core.telegram.org/bots/api](https://core.telegram.org/bots/api)
- **Telegram BotFather Instructions**: [https://core.telegram.org/bots#6-botfather](https://core.telegram.org/bots#6-botfather)
- **FFmpeg Documentation**: [https://ffmpeg.org/ffmpeg.html](https://ffmpeg.org/ffmpeg.html)
- **OpenAI GPT**: [https://openai.com/api/](https://openai.com/api/)