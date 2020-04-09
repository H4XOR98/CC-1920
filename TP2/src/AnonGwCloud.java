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
        if(address != null && conteudo != null && tamanho > 0){
            conteudos.put(address,conteudo);
            tamanhos.put(address,tamanho);
        }
    }

    public synchronized byte[] getConteudo(String address){
        if(conteudos.containsKey(address)){
            byte[] conteudo = conteudos.get(address).clone();
            conteudos.remove(address);
            return conteudo;
        }
        return null;
    }

    public synchronized int getTamanho(String address){
        if(tamanhos.containsKey(address)){
            int tamanho = tamanhos.get(address);
            tamanhos.remove(address);
            return tamanho;
        }
        return 0;
    }

}
