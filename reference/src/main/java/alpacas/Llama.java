package alpacas;

public class Llama {
    public String name;
    public int weight;


    public Llama(String name, int weight) {
        this.name = name;
        this.weight = weight;

        if (name == null) {
            throw new IllegalArgumentException("Llama has to have a name");
        }
        if (weight <= 5) {
            throw new IllegalArgumentException("Llama can't be five kg oder under five kg");
        } else if (weight >= 271) {
            throw new IllegalArgumentException("Llama can't be Two hundred and seventy-one kg or over Two hundred and seventy-one kg");
        }

    }

    public String getName() {
        return name;
    }

    public int getWeight() {
        return weight;
    }

    public boolean isHealthyWeight() {
        if (weight <= 129) {
            System.out.println("The llama is underweight it needs to be 130 kg.");
            return false;
        } else if (weight >= 201){
            System.out.println("The llama is overweight it needs to be under 201 kg.");
            return false;
        } else {
            System.out.println("The llama is healthy.");
            return true;
        }
    }
}
