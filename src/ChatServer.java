
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author MASTER
 */
public class ChatServer {

    private final int PORT = 8089;
    private ServerSocket serverSocket;
    private final List<ClientSocket> clients = new LinkedList();

    public void start() throws IOException {

        serverSocket = new ServerSocket(PORT);
        System.out.println("Servidor Inicializado na Porta! " + PORT);
        System.out.println("Aguardando Conexao ......");

        ClientConectionLoop();
    }

    private void ClientConectionLoop() throws IOException {

        while (true) {

            ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
            System.out.println("Cliente Conectado " + clientSocket.getRemoteSocketAddress());

            InetAddress host = serverSocket.getInetAddress();
            String msg = "Voce esta Conectado no Servidor JavaSD";

            new Thread(() -> {
                try {
                    clientMessageLoop(clientSocket);
                } catch (IOException ex) {
                    System.out.println("Problemas ao encerrar a Conexao");
                }
            }).start();
            clientSocket.sendMsg(msg);

        }

    }

    public void clientMessageLoop(ClientSocket clientSocket) throws IOException {

        String msg;

        String temp = (clientSocket.getMessage());

        JSONObject json;
        JSONParser parser = new JSONParser();
        CarregaUsuarios listarUsuarios = new CarregaUsuarios();
        Usuario user = new Usuario();

        //arrumar ancadeamento para as exceções
        try {
            json = (JSONObject) parser.parse(temp);
            String nome = (String) json.get("ra");
            String senha = (String) json.get("senha");
            System.out.println(nome);
            System.out.println(senha);

            //System.out.println("Json "+ user.getNome());
            //clientSocket.setLogin(clientSocket.getMessage());
            user = listarUsuarios.localizarUsuario(nome, senha);
            System.out.println(user.getNome());//captura a exceção sem precisar usar um id throw new
            clientSocket.setUsuario(user);
            clients.add(clientSocket);
            while ((msg = clientSocket.getMessage()) != null) {
                if ("sair#$%".equalsIgnoreCase(msg)) {
                    System.out.println("Socket fechado para o cliente " + clientSocket.getRemoteSocketAddress());
                    clientSocket.sendMsg(null);
                    break;
                }
                   if ("lista".equalsIgnoreCase(msg)) {
                    
                    clientSocket.sendMsg(clients.toString().replace("[", "").replace("]","").replace(" ", ""));
                    
                }
                //clientSocket.sendMsg("[ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ]" + " Servidor: " + msg);
                Broadcasting(clientSocket, msg);
            }

        } catch (ParseException ex) {
            System.out.println("Formato incorreto do arquivo JSON");
            System.out.println("Socket fechado para o cliente " + clientSocket.getRemoteSocketAddress());
            clientSocket.sendMsg(null);
            clientSocket.closeInOut();
        } catch (NullPointerException e) {

            System.out.println("Cliente Nao Encontrado!");
            System.out.println("Socket fechado para o cliente " + clientSocket.getRemoteSocketAddress());
            clientSocket.sendMsg(null);
            clientSocket.closeInOut();
        }

        /*Movido para esse método,´pois no método ClientConectionLoop
          ele trava o cliente. Tanta atribuição do nome quanto a adição
          na lista de clientes tem que ficar fora do while.
         */
        //return;
    }

    private void Broadcasting(ClientSocket sender, String msg) {

        Iterator<ClientSocket> iterator = clients.iterator();
        while (iterator.hasNext()) { //percorres a list clients
            ClientSocket clientSocket = iterator.next();

            if (!sender.equals(clientSocket)) {
                //parâmetro sendo verifica o remetente da msg, assim evita enviar o mensagem pra vc mesmo
                if (!clientSocket.sendMsg("[ " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " ]" + sender.getUsuario().getNome() + " : " + msg)) {
                    //caso servidor tente mandar a mensagem e o cliente não respoder ele remove o cliente da lista
                    iterator.remove();
                }
            }
        }
    }

    public static void main(String[] args) {

        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException ex) {
            System.out.println("Erro ao Iniciar o servidor :" + ex.getMessage());
        }
        System.out.println("Servidor finalizado!");
    }
}
