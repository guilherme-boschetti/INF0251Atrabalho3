# INF0251Atrabalho3
Trabalho 3 da Disciplina INF0251A - UCS

Aplicativo para consumir uma API de moedas e informar o valor de uma determinada moeda escolhida pelo usuário em Reais; por exemplo pode-se escolher saber quanto está valendo o Dólar Comercial em Reais.
O objetivo deste trabalho era construir um aplicativo que tivesse alguma utilidade e que usasse os seguintes recursos: 1) Consumir API utilizando Retrofit; 2) Utilizar Banco de Dados Local SQLite; 3) Utilizar Serviço Background (Service); 4) Possuir menu de deslize lateral (Navigation Drawer).
Este aplicativo permite ao usuário escolher uma moeda na qual será apresentado seu valor em Reais. Cada vez que o aplicativo detectar uma alteração no valor da moeda, o novo valor é salvo no Banco de Dados; para que posteriormente possa ser consultado o histórico de alterações da moeda, ou até obter-se o valor da média dessas alterações no valor da moeda. Além disso é possível ativar um serviço que roda em background e definir um intervalo de tempo para que a API de moedas seja consultada; caso o app detecte que o valor da moeda sofreu alteração durante uma consulta em background, é emitida uma notificação ao usuário.

=======================================

Work 3 of Discipline INF0251A - UCS

Application to consume a currency API and inform the value of a particular currency chosen by the user in Reais; for example, you can choose to know how much the Commercial Dollar in Reais is worth.
The objective of this work was to build an application that had some use and that used the following resources: 1) Consuming API using Retrofit; 2) Use Local SQLite Database; 3) Use Background Service (Service); 4) Have a side slide menu (Navigation Drawer).
This application allows the user to choose a currency in which to display its value in Reais. Each time the application detects a change in the currency value, the new value is saved in the Database; so that you can later consult the history of currency changes, or even obtain the average value of those changes in the currency value. In addition, it is possible to activate a service that runs in the background and define a time interval for the currency API to be consulted; if the app detects that the currency value has changed during a background consult, a notification is issued to the user.
