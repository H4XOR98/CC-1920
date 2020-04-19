import java.util.HashMap;
import java.util.Map;

public class AnonGwCloud {
    private Map<String, byte[]> conteudos;
    private Map<String, Integer> tamanhos;

    public AnonGwCloud(){
        conteudos = new HashMap<>();
        tamanhos = new HashMap<>();
    }

    public synchronized void inserirConteudo (String address, byte[] conteudo, int tamanho){
        System.out.println("Início da execução de [AnonGwCloud -> inserirConteudo]");
        if(address != null && conteudo != null && tamanho > 0){
            conteudos.put(address,conteudo);
            tamanhos.put(address,tamanho);
            System.out.println(tamanho);
            System.out.println(address);
            System.out.println(conteudo.toString());
        }else{
            System.out.println("Os valores são nulos");
        }
        System.out.println("Fim da execução de [AnonGwCloud -> inserirConteudo]");
    }

    public synchronized byte[] getConteudo(String address){
        System.out.println("Início da execução de [AnonGwCloud -> getConteudo]");
        if(conteudos.containsKey(address)){
            byte[] conteudo = conteudos.get(address).clone();
            conteudos.remove(address);
            System.out.println("Sucesso");
            return conteudo;
        }else {
            System.out.println("O endereço não está registado no sistema");
        }
        System.out.println("Fim da execução de [AnonGwCloud -> getConteudo]");
        return null;
    }

    public synchronized int getTamanho(String address){
        System.out.println("Início da execução de [AnonGwCloud -> getTamanho]");
        if(tamanhos.containsKey(address)){
            int tamanho = tamanhos.get(address);
            tamanhos.remove(address);
            System.out.println("Sucesso");
            return tamanho;
        }else {
            System.out.println("O endereço não está registado no sistema");
        }
        System.out.println("Fim da execução de [AnonGwCloud -> getTamanho]");
        return 0;
    }

}
