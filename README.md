# **Agente Conversacional com Reconhecimento de Voz e Integração com LLM**

## **Visão Geral**

Este projeto consiste em um agente conversacional desenvolvido em **Spring Boot** e integrado ao **Telegram**, com suporte para mensagens de texto e voz. O agente é capaz de:
1. Receber mensagens de texto e voz de um usuário no Telegram.
2. Converter mensagens de voz no formato OGG para MP3.
3. Utilizar um serviço de **Reconhecimento de Voz** para converter a mensagem de áudio em texto.
4. Enviar o texto para um **modelo de linguagem natural** (*Large Language Model* ou LLM) utilizando **Spring.AI**.
5. Retornar uma resposta gerada pela LLM ao usuário no Telegram.

Este projeto explora conceitos de **Processamento de Linguagem Natural (PLN)**, **Reconhecimento de Fala** e **Inteligência Artificial Conversacional**, oferecendo uma solução prática e acadêmica que pode ser utilizada em diversos cenários de interação com o usuário.

---

## **Tecnologias Utilizadas**
1. **Spring Boot**: Framework utilizado para a construção da aplicação Java.
2. **Telegram Bots API**: Interface usada para conectar e interagir com os usuários no Telegram.
3. **Spring.AI**: Integração com modelos de linguagem natural, como os fornecidos pela OpenAI, para gerar respostas baseadas em texto.
4. **JAVE**: Biblioteca para converter arquivos de áudio do formato OGG para MP3.
5. **Reconhecimento de Fala**: Uso da API da **Groq** para realizar a conversão de áudio em texto.

---

## **Arquitetura da Solução**

A solução segue uma arquitetura modular que facilita a integração de diferentes serviços e a manutenção do código:

### **1. Interação com o Telegram**
O agente é construído utilizando a biblioteca **telegrambots-abilities**, que permite organizar e gerenciar habilidades (abilities) de forma modular. A classe principal `PlnAgent` herda de `AbilityBot` e define as interações básicas com o usuário, como:
- Iniciar o bot com o comando `/start`.
- Responder a mensagens de voz.
- Responder a mensagens de texto.

A partir dessa interação, o agente obtém as mensagens de voz ou texto enviadas pelo usuário e processa essas entradas.

### **2. Processamento de Mensagens de Voz**
Quando o usuário envia uma mensagem de voz, o bot segue os seguintes passos:
1. O arquivo de áudio, no formato OGG, é baixado do Telegram.
2. O áudio é convertido de OGG para MP3 utilizando a biblioteca **JAVE**.
3. O arquivo MP3 é enviado para a API de **Reconhecimento de Fala** (Groq), que converte o áudio em texto.

### **3. Integração com LLM (Processamento de Linguagem Natural)**
Após a conversão da voz para texto, o texto gerado é enviado para um **modelo de linguagem natural** via **Spring.AI**. Esse modelo é responsável por analisar a mensagem e gerar uma resposta baseada em algoritmos avançados de **Machine Learning** e **Inteligência Artificial**.

Os **LLMs** são modelos treinados em grandes volumes de dados e capazes de entender e gerar respostas coerentes em linguagem natural. No projeto, usamos um modelo baseado na API OpenAI para gerar respostas aos usuários.

### **4. Resposta ao Usuário**
Finalmente, a resposta gerada pelo modelo LLM é enviada de volta ao usuário no Telegram como uma mensagem de texto.

---

## **Fluxo de Execução**
1. **Início**: O usuário interage com o bot enviando uma mensagem de texto ou voz.
2. **Processamento de Texto**: Se for uma mensagem de texto, o bot envia o texto diretamente para a LLM.
3. **Processamento de Voz**:
   - O bot baixa o arquivo de voz.
   - O arquivo OGG é convertido para MP3.
   - O MP3 é enviado para a API de reconhecimento de fala, que converte o áudio em texto.
   - O texto resultante é enviado para a LLM para processamento.
4. **Resposta**: A resposta da LLM é enviada ao usuário como uma mensagem de texto no Telegram.

---

## **Conceitos**

### **1. Processamento de Linguagem Natural (PLN)**
O **PLN** refere-se à aplicação de algoritmos de Inteligência Artificial para entender e gerar linguagem humana. No contexto deste projeto, utilizamos um modelo LLM para interpretar as mensagens enviadas pelos usuários e gerar respostas adequadas. Modelos LLM, como os fornecidos pela OpenAI, são treinados em grandes volumes de dados textuais e são capazes de gerar respostas com base em contextos complexos.

#### **Relevância**:
O PLN é uma área fundamental da IA, usada em tarefas como **análise de sentimentos**, **extração de entidades**, **tradução automática** e **respostas automáticas**. Este projeto aplica o PLN ao contexto de assistentes virtuais.

### **2. Reconhecimento de Fala**
O **Reconhecimento de Fala** é a tecnologia que permite converter fala em texto. No projeto, utilizamos um serviço de reconhecimento de fala (API Groq) para transcrever áudios enviados pelos usuários para texto, que é posteriormente processado por um LLM.

#### **Relevância**:
O reconhecimento de fala é uma área essencial da interação humano-computador e é utilizado em muitos contextos, como assistentes virtuais (Siri, Alexa), transcrição de áudios e sistemas de ditado. Ele envolve técnicas avançadas de **Machine Learning** e **Redes Neurais**, treinadas para identificar padrões de fala e mapeá-los para texto.

### **3. Conversão de Áudio (OGG para MP3)**
Os arquivos de áudio enviados pelo Telegram estão no formato OGG, que não é diretamente compatível com a maioria dos serviços de reconhecimento de voz. Portanto, é necessário realizar a conversão para MP3 antes de enviá-los ao serviço de transcrição. A conversão é realizada utilizando a biblioteca **JAVE**, que facilita o processo de transformação entre diferentes formatos de mídia.

#### **Relevância**:
A manipulação de arquivos de áudio e vídeo é fundamental em diversas áreas da computação, como multimídia, compressão de dados e transmissão de conteúdo. A conversão de formatos é uma operação comum em sistemas que processam áudio e vídeo de várias fontes.

---

## **Como Executar**

### **Pré-requisitos**
- **Java 11+** instalado
- **Maven** instalado
- **Token do Telegram Bot** (disponível via BotFather)
- **Chave de API** para o serviço de reconhecimento de voz (Groq)

### **Passos para Execução**
1. Clone este repositório:
   ```bash
   git clone https://github.com/seu-repositorio
   cd seu-repositorio
   ```

2. Compile o projeto com Maven:
   ```bash
   mvn clean install
   ```

3. Execute a aplicação:
   ```bash
   mvn spring-boot:run
   ```

4. Envie uma mensagem de voz ou texto para o bot no Telegram e veja o agente em ação!

---

## **Desafios Adicionais**
- Adicione suporte para reconhecimento de múltiplos idiomas.
- Melhore a análise de sentimentos nas respostas do bot.
- Implemente uma interface de administração para monitorar as interações do bot.

---

## **Licença**
Este projeto é licenciado sob os termos da [MIT License](LICENSE).
