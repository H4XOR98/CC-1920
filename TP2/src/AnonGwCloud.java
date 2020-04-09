import java.util.HashMap;
import java.util.Map;

public class AnonGwCloud {
    private Map<String, byte[]> conteudos;
    private Map<String, Integer> tamanhos;

    public AnonGwCloud(){
        conteudos = new HashMap<>();
    }

    public synchronized void inserirFicheiro (String address, byte[] ficheiro, int tamanho){
        if(address != null && ficheiro!= null && tamanho > 0){
            conteudos.put(address,ficheiro);
            tamanhos.put(address,tamanho);
        }
    }

    public synchronized byte[] getFicheiro(String address){
        if(conteudos.containsKey(address)){
            byte[] ficheiro = conteudos.get(address).clone();
            conteudos.remove(address);
            return ficheiro;
        }
        return null;
    }

    public synchronized int getTamanho(String address){
        if(conteudos.containsKey(address)){
            int tamanho = tamanhos.get(address);
            tamanhos.remove(address);
            return tamanho;
        }
        return 0;
    }

}
