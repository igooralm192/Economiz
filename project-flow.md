# Fluxo do Projeto:
![alt-text](https://github.com/igooralm192/ProjetoPOO/blob/master/images/flow.png "Fluxograma do projeto")
### Resumo:
Tendo a main branch (main_activity) e a tarefa da semana (issue global), cria-se as tarefas (issues de uma issue global) para cada bug a ser corrigido, funcionalidade a ser adicionada ou alteração a ser feita.

Para cada tarefa cria-se uma branch para realizar as atividades. 
Após feito todos os ajustes e commits referencia a issue da tarefa, o próximo passo é dar **_push_** na branch local para o repositório.
Se não há mais nada a ser feito, deve-se fechar a issue e solicitar o pull request (main branch <= branch tarefa).

Tendo criado o pull request, verificar se não há mais atividades:
- Se não houver então efetue o merge da branch nova para a main branch.
- Se há então:
  - Reabra a issue filha que já foi fechada e, caso tenha, adicione subtarefas nela com checkboxs.
