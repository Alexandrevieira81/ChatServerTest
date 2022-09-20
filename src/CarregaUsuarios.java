/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author MASTER
 */
public class CarregaUsuarios {

    private Usuario usuarios[];

    public CarregaUsuarios() {
        this.usuarios = new Usuario[3];
        this.incializarUsuario();
    }

    public void incializarUsuario() {

        Usuario user1 = new Usuario("Alex", "1488880", "123");
        Usuario user2 = new Usuario("Ive", "4788880", "321");
        Usuario user3 = new Usuario("Breno","1202020", "123");

        this.usuarios[0] = user1;
        this.usuarios[1] = user2;
        this.usuarios[2] = user3;

    }

    public Usuario localizarUsuario(String login, String senha) {
        int i;
        for (i = 0; i < this.usuarios.length; i++) {

            if ((this.usuarios[i].getRa().equals(login)) && (this.usuarios[i].getSenha().equals(senha))) {

                return this.usuarios[i];

            }

        }
        if (i == this.usuarios.length) {

            System.out.println("Usuario Invalido");

        }
        return null;
    }

}
