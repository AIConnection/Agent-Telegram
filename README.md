# **Documento de Projeto: Agente Virtual Inteligente com Reconhecimento de Voz e Integração com LLM**

## **1. Introdução**

Este documento apresenta, de forma didática, todos os aspectos do desenvolvimento de um agente virtual inteligente que interage com usuários via **Telegram**, utilizando **Spring Boot**, **Spring.AI**, e a biblioteca **telegrambots-abilities**. O agente processa tanto mensagens de texto quanto de voz, utilizando a API da **Groq** para reconhecimento de fala e um **Large Language Model (LLM)** para gerar respostas contextuais.

O projeto explora a aplicação de conceitos de **Inteligência Artificial (IA)**, **Processamento de Linguagem Natural (PLN)**, **Reconhecimento de Fala**, e **Engenharia de Software**, aplicados em um sistema de agentes inteligentes.

---

## **2. Requisitos do Projeto**

### **2.1. Funcionais**
- O sistema deve receber e processar mensagens de texto enviadas pelos usuários no Telegram.
- O sistema deve receber mensagens de voz, convertê-las em texto e processar a transcrição.
- O sistema deve integrar-se com uma **LLM** (Large Language Model) para gerar respostas inteligentes baseadas nas mensagens dos usuários.
- O sistema deve retornar a resposta gerada pela **LLM** ao usuário no Telegram, tanto para mensagens de texto quanto de voz.

### **2.2. Não Funcionais**
- O sistema deve ser modular e escalável, permitindo fácil adição de novos componentes.
- O tempo de resposta para as interações deve ser o menor possível, considerando a integração com serviços externos.
- A arquitetura deve facilitar a substituição de serviços externos, como a API de reconhecimento de voz, sem grandes modificações.
- O sistema deve ser facilmente testável, com suporte para testes unitários e de integração.

---

## **3. Arquitetura do Sistema**

### **3.1. Arquitetura Geral**

A arquitetura do sistema é composta por três componentes principais:

1. **Cliente (Telegram)**: O Telegram serve como interface de comunicação com os usuários, onde eles enviam mensagens de texto ou voz, e recebem respostas do bot.

2. **Servidor Backend (Spring Boot)**: O servidor backend, desenvolvido com **Spring Boot**, processa as mensagens dos usuários e utiliza diversos módulos para realizar diferentes funções, como conversão de áudio, reconhecimento de fala e processamento de linguagem natural.

3. **Serviços Externos**: O sistema se integra a serviços externos, como a API da **Groq** para reconhecimento de fala e o **Spring.AI** para gerar respostas a partir de modelos LLM.

### **3.2. Componentes da Arquitetura**

- **PlnAgent**: Componente principal que gerencia as interações com o Telegram, utilizando a biblioteca **telegrambots-abilities**. Ele define as habilidades do bot, como receber comandos de texto e processar mensagens de voz.

- **VoiceRecognitionService**: Serviço que integra-se à **API Groq** para transcrição de voz, convertendo áudios MP3 em texto.

- **AudioConversionService**: Responsável por converter os arquivos de áudio de OGG (formato padrão do Telegram) para MP3, permitindo que o áudio seja processado pelo serviço de reconhecimento de fala.

- **Spring.AI Integration**: Módulo que realiza a comunicação com a **LLM**, gerando respostas inteligentes baseadas no texto transcrito ou nas mensagens de texto dos usuários.

---

## **4. Conceitos Aplicados**

### **4.1. Inteligência Artificial e Processamento de Linguagem Natural (PLN)**

O sistema aplica **Inteligência Artificial** por meio do **Processamento de Linguagem Natural (PLN)**, utilizando um **Large Language Model (LLM)**. Esses modelos, como o GPT, são treinados em grandes quantidades de dados e são capazes de entender e gerar linguagem natural de forma sofisticada. Neste projeto, o LLM é utilizado para interpretar as mensagens dos usuários e gerar respostas inteligentes.

### **4.2. Reconhecimento de Fala**

O **Reconhecimento de Fala** converte áudio em texto, permitindo que o bot processe mensagens de voz enviadas pelos usuários. Para essa funcionalidade, o projeto utiliza a **API Groq**, que transcreve arquivos de áudio em MP3 para texto.

### **4.3. Conversão de Áudio (OGG para MP3)**

Os arquivos de áudio enviados pelo Telegram estão no formato OGG, que não é suportado diretamente por todos os serviços de reconhecimento de fala. Portanto, o áudio é convertido para MP3 usando o **JAVE** (Java Audio Video Encoder), permitindo que o arquivo seja processado pela API de transcrição.

---

## **5. Frameworks Utilizados**

### **5.1. Spring Boot**
O **Spring Boot** é o framework principal para o desenvolvimento da aplicação backend. Ele facilita a criação de APIs e sistemas web robustos e escaláveis, com suporte para integração de múltiplos serviços e bibliotecas.

### **5.2. Spring.AI**
**Spring.AI** é utilizado para integrar o sistema com **Large Language Models (LLMs)**, permitindo que o bot processe entradas de linguagem natural e gere respostas contextuais de forma eficiente.

### **5.3. Telegram Bots API e telegrambots-abilities**
A biblioteca **telegrambots-abilities** simplifica o desenvolvimento de bots no Telegram. Ela permite definir habilidades (abilities) de forma modular, como responder a comandos de texto ou processar mensagens de voz, abstraindo grande parte da complexidade da API do Telegram.

### **5.4. JAVE**
O **JAVE** é utilizado para converter os arquivos de áudio OGG, que são o padrão do Telegram, para o formato MP3. Isso é necessário para que o áudio seja enviado para o serviço de transcrição de voz da API da Groq.

---

## **6. Serviços Utilizados**

### **6.1. API Groq**
A **API Groq** é usada para realizar o reconhecimento de fala, convertendo áudios MP3 em texto. Esse texto é utilizado como entrada para o modelo de linguagem natural, que gera uma resposta inteligente.

#### **Como Configurar a API Groq**
1. Acesse [Groq Console](https://console.groq.com) e crie uma conta.
2. Gere uma chave de API em **API Keys**.
3. Configure a chave de API no arquivo `application.properties`:
   ```properties
   spring.ai.openai.api-key=<YOUR_GROQ_API_KEY>
   ```

### **6.2. OpenAI GPT via Spring.AI**
O **Spring.AI** integra o bot com um **Large Language Model (LLM)**, permitindo que ele gere respostas inteligentes com base no contexto da interação. Esse serviço é configurado para processar tanto mensagens de texto quanto de voz transcrita.

---

## **7. Engenharia de Software Aplicada**

### **7.1. Princípios SOLID**
Os princípios **SOLID** foram aplicados durante o desenvolvimento do sistema, garantindo uma arquitetura modular e de fácil manutenção:

- **S - Single Responsibility Principle**: Cada módulo tem uma única responsabilidade. Por exemplo, o `VoiceRecognitionService` lida apenas com a transcrição de voz, enquanto o `PlnAgent` é responsável por interagir com o Telegram.
  
- **O - Open/Closed Principle**: O sistema está aberto para extensão, permitindo adicionar novas funcionalidades sem modificar o código existente. Isso foi possível por meio da injeção de dependências e de interfaces claras.

- **L - Liskov Substitution Principle**: O sistema foi desenvolvido de forma que componentes possam ser substituídos sem comprometer seu comportamento, permitindo a substituição de serviços de terceiros, como a API de reconhecimento de voz.

- **I - Interface Segregation Principle**: As interfaces foram projetadas de forma que os módulos utilizam apenas os métodos que precisam, promovendo a separação de responsabilidades.

- **D - Dependency Inversion Principle**: O projeto utiliza **injeção de dependência**, o que facilita a substituição de implementações concretas e a realização de testes unitários.

### **7.2. Arquitetura em Camadas**
A aplicação segue uma **arquitetura em camadas**, separando a lógica de negócios da camada de comunicação com APIs e da camada de infraestrutura. Essa abordagem permite uma fácil manutenção e facilita a escalabilidade do sistema.

### **7.3. Modularidade e Testabilidade**
O projeto foi estruturado para garantir a modularidade, onde cada funcionalidade (como reconhecimento de fala ou integração com a LLM) é encapsulada em seu próprio módulo. Isso facilita a implementação de testes unitários e a manutenção do sistema.

### **7.4. Escalabilidade**
Graças à sua modularidade e ao uso de boas práticas de engenharia de software, o sistema pode ser facilmente escalado para incluir novas funcionalidades, como a integração com outros serviços de PLN ou suporte para múltiplos idiomas.

### **