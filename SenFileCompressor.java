import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SenFileCompressor {
    public static void main(String[] args) {
        // Vérifier si des arguments sont fournis
        if (args.length < 1) {
            printUsage();
            return;
        }

        // Analyser les arguments
        String option = args[0];
        switch (option) {
            case "-c":
                compressFiles(args); // Compression des fichiers
                break;
            case "-d":
                decompressFile(args); // Décompression du fichier
                break;
            case "-h":
                printUsage(); // Afficher l'aide
                break;
            default:
                System.out.println("Option non reconnue.");
                printUsage();
        }
    }

    // Partie : Archivage des données
    private static void compressFiles(String[] args) {
        // Vérifier s'il y a au moins deux fichiers à compresser
        if (args.length < 3) {
            System.out.println("Veuillez fournir au moins deux fichiers à compresser.");
            return;
        }

        // Création de liste pour stocker les noms des fichiers à compresser
        List<String> filesToCompress = new ArrayList<>();

        // Définition d'un nom par défaut pour le fichier de sortie de l'archive compressée
        String outputFileName = "archive.sfc";

        // Définition d'une chaîne vide pour stocker le chemin du répertoire de sortie
        String outputDirectory = "";

        // Analyser les arguments pour extraire les noms des fichiers à compresser et le chemin de sortie
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("-o") && i + 1 < args.length) {
                outputFileName = args[i + 1];
                i++; // Ignorer l'argument suivant qui est le nom de fichier de sortie
            } else if (args[i].equals("-r") && i + 1 < args.length) {
                outputDirectory = args[i + 1];
                i++; // Ignorer l'argument suivant qui est le chemin de sortie
            } else if (args[i].equals("-f")) {
                // L'option -f doit être utilisée en conjonction avec l'option -r
                if (outputDirectory.isEmpty()) {
                    System.out.println("L'option -f doit être utilisée avec l'option -r.");
                    return;
                }
            } else {
                filesToCompress.add(args[i]);
            }
        }

        // Compresser les fichiers
        try {
            // Vérifier si le répertoire de sortie existe et le créer si nécessaire
            if (!outputDirectory.isEmpty()) {
                File directory = new File(outputDirectory);
                if (!directory.exists()) {
                    if (argsContainsOption(args, "-f")) {
                        directory.mkdirs();
                    } else {
                        System.out.println("Le répertoire de sortie spécifié n'existe pas.");
                        return;
                    }
                }
                outputFileName = outputDirectory + File.separator + outputFileName;
            }

            // Compression des fichiers
            try (FileOutputStream fos = new FileOutputStream(outputFileName);
                 BufferedOutputStream bos = new BufferedOutputStream(fos);
                 ObjectOutputStream oos = new ObjectOutputStream(bos)) {

                for (String fileName : filesToCompress) {
                    File file = new File(fileName);
                    if (file.exists()) {
                        byte[] data = readFileBytes(file);
                        oos.writeObject(new ArchiveEntry(file.getName(), data));
                    } else {
                        System.out.println("Le fichier '" + fileName + "' n'existe pas.");
                    }
                }

                System.out.println("Compression réussie. Archive créée : " + outputFileName);
            } catch (IOException e) {
                System.out.println("Une erreur s'est produite lors de la compression : " + e.getMessage());
            }
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    // Partie : Compression des données
    private static void decompressFile(String[] args) {
        // Vérifier si le fichier à décompresser est fourni
        if (args.length < 2) {
            System.out.println("Veuillez fournir le fichier à décompresser.");
            return;
        }

        String inputFile = args[1];

        // Décompresser le fichier
        try (FileInputStream fis = new FileInputStream(inputFile);
             BufferedInputStream bis = new BufferedInputStream(fis);
             ObjectInputStream ois = new ObjectInputStream(bis)) {

            while (true) {
                try {
                    ArchiveEntry entry = (ArchiveEntry) ois.readObject();
                    writeFile(entry.getFileName(), entry.getData());
                } catch (EOFException e) {
                    break; // Fin du fichier atteinte
                }
            }

            System.out.println("Décompression réussie.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Une erreur s'est produite lors de la décompression : " + e.getMessage());
        }
    }

    // Méthode utilitaire pour lire les données d'un fichier
    private static byte[] readFileBytes(File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file);
             BufferedInputStream bis = new BufferedInputStream(fis)) {

            byte[] data = new byte[(int) file.length()];
            bis.read(data);
            return data;
        }
    }

    // Méthode utilitaire pour écrire les données dans un fichier
    private static void writeFile(String fileName, byte[] data) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(fileName);
             BufferedOutputStream bos = new BufferedOutputStream(fos)) {

            bos.write(data);
        }
    }

    // Méthode utilitaire pour afficher l'aide
    private static void printUsage() {
        System.out.println("Instructions d'utilisation :");
        System.out.println("java SenFileCompressor –h                    Afficher l’aide");
        System.out.println("java SenFileCompressor –c <fichiers> [options]     Compression des fichiers");
        System.out.println("java SenFileCompressor –d <fichier.sfc> [options]  Décompression du fichier");
        System.out.println("Options :");
        System.out.println("-r <chemin>            Chemin vers le répertoire de destination");
        System.out.println("-f                     Créer le chemin s'il n'existe pas");
        System.out.println("-v                     Verbosité");
    }

    // Classe pour représenter une entrée d'archive
    private static class ArchiveEntry implements Serializable {
        private final String fileName;
        private final byte[] data;

        public ArchiveEntry(String fileName, byte[] data) {
            this.fileName = fileName;
            this.data = data;
        }

        public String getFileName() {
            return fileName;
        }

        public byte[] getData() {
            return data;
        }
    }

    // Méthode utilitaire pour vérifier si un argument est présent dans la liste des arguments
    private static boolean argsContainsOption(String[] args, String option) {
        for (String arg : args) {
            if (arg.equals(option)) {
                return true;
            }
        }
        return false;
    }
}
