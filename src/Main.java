import java.io.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

class Voto {
    int id;
    int votanteID;
    int candidatoID;
    String timestamp; // "hh:mm:ss"

    public Voto(int id, int votanteID, int candidatoID, String timestamp) {
        this.id = id;
        this.votanteID = votanteID;
        this.candidatoID = candidatoID;
        this.timestamp = timestamp;
    }

    int getId() {
        return id;
    }
    int getVotanteID() {
        return votanteID;
    }
    int getCandidatoID() {
        return candidatoID;
    }
    String getTimestamp() {
        return timestamp;
    }
}

class Candidato {
    int idCandidato;
    String idNombre;
    String partido;
    Queue<Voto> votosRecibidos;

    public Candidato(int idCandidato, String idNombre, String partido) {
        this.idCandidato = idCandidato;
        this.idNombre = idNombre;
        this.partido = partido;
        this.votosRecibidos = new LinkedList<>();
    }
    int getIdCandidato() {
        return idCandidato;
    }
    String getIdNombre() {
        return idNombre;
    }
    String getPartido() {
        return partido;
    }
    Queue<Voto> getVotosRecibidos() {
        return votosRecibidos;
    }
    public void agregarVoto(Voto voto) {
        votosRecibidos.offer(voto);
    }
}

class Votante {
    int id;
    String nombre;
    Boolean yaVoto;
    public Votante(int id, String nombre, Boolean yaVoto) {
        this.id = id;
        this.nombre = nombre;
        this.yaVoto = false;
    }
    void marcarcomoVotado() {
        this.yaVoto = true;
    }
    int getId() {
        return id;
    }
    String getNombre() {
        return nombre;
    }
    Boolean getYaVoto() {
        return yaVoto;
    }
}

class urnaElectoral {
    Queue<Candidato> listaCandidatos;
    Stack<Voto> historialVotos;
    Queue<Voto> votosReportados;
    int idCounter;
    Set<Integer> idsVotantesRegistrados = new HashSet<>();
    Set<Integer> idsCandidatosRegistrados = new HashSet<>();

    public urnaElectoral() {
        this.listaCandidatos = new LinkedList<>();
        this.historialVotos = new Stack<>();
        this.votosReportados = new LinkedList<>();
        this.idCounter = 0;
    }

    public boolean agregarCandidato(Candidato candidato) {
        if (idsCandidatosRegistrados.contains(candidato.getIdCandidato())) {
            System.out.println("Error: ID de candidato duplicado - " + candidato.getIdCandidato());
            return false;
        }
        listaCandidatos.offer(candidato);
        idsCandidatosRegistrados.add(candidato.getIdCandidato());
        return true;
    }

    public boolean registrarVotante(Votante votante) {
        if (idsVotantesRegistrados.contains(votante.getId())) {
            System.out.println("Error: ID de votante duplicado - " + votante.getId());
            return false;
        }
        idsVotantesRegistrados.add(votante.getId());
        return true;
    }

    boolean verificarVotante(Votante votante) {
        return votante.getYaVoto();
    }

    boolean registrarVoto(Votante votante, int candidatoID) {
        if (verificarVotante(votante)) {
            System.out.println("El voto " + votante.getId() + " ya esta registrado");
            return false;
        }
        else {
            String timeStamp = java.time.LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            Voto votoNuevo = new Voto(idCounter++, votante.getId(), candidatoID, timeStamp);
            for(Candidato candidato : listaCandidatos) {
                if(candidato.getIdCandidato() == candidatoID) {
                    candidato.agregarVoto(votoNuevo);
                    historialVotos.push(votoNuevo);
                    votante.marcarcomoVotado();
                    System.out.println("voto registrado");
                    return true;
                }
            }
        }
        System.out.println("El candidato " + candidatoID + " no se ha encontrado en el registro");
        return false;
    }

    boolean reportarVoto(Candidato candidato, int idVoto) {
        Queue<Voto> votos = candidato.getVotosRecibidos();
        for (Voto voto : votos) {
            if(voto.getId() == idVoto) {
                votos.remove(voto);
                votosReportados.add(voto);
                System.out.println("voto reportado");
                return true;
            }
        }
        return false;
    }

    Map<String, Integer> obtenerResultados() {
        Map<String, Integer> resultados = new HashMap<>();
        for (Candidato candidato : listaCandidatos) {
            resultados.put(candidato.getIdNombre(), candidato.getVotosRecibidos().size());
        }
        return resultados;
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        urnaElectoral urna = new urnaElectoral();

        System.out.println("Sistema de Votación Electrónica");

        System.out.println("\n--- Ingreso de Candidatos ---");
        boolean agregarMasCandidatos = true;
        while (agregarMasCandidatos) {
            System.out.print("ID del candidato: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Nombre del candidato: ");
            String nombre = scanner.nextLine();
            System.out.print("Partido político: ");
            String partido = scanner.nextLine();

            Candidato nuevoCandidato = new Candidato(id, nombre, partido);
            if (!urna.agregarCandidato(nuevoCandidato)) {
                System.out.println("Intente con un ID diferente.");
                continue;
            }

            System.out.print("¿Desea agregar otro candidato? (s/n): ");
            agregarMasCandidatos = scanner.nextLine().equalsIgnoreCase("s");
        }

        System.out.println("\n--- Ingreso de Votantes ---");
        boolean agregarMasVotantes = true;
        while (agregarMasVotantes) {
            System.out.print("ID del votante: ");
            int id = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Nombre del votante: ");
            String nombre = scanner.nextLine();

            Votante votante = new Votante(id, nombre, false);
            if (!urna.registrarVotante(votante)) {
                System.out.println("Intente con un ID diferente.");
                continue;
            }

            System.out.println("\nCandidatos disponibles:");
            for (Candidato c : urna.listaCandidatos) {
                System.out.println(c.getIdCandidato() + " - " + c.getIdNombre() + " (" + c.getPartido() + ")");
            }

            System.out.print("\nSeleccione el ID del candidato por el que desea votar: ");
            int candidatoId = scanner.nextInt();
            scanner.nextLine();

            if (urna.registrarVoto(votante, candidatoId)) {
                System.out.println("¡Voto registrado");
            }

            System.out.print("\n¿Desea registrar otro votante? (s/n): ");
            agregarMasVotantes = scanner.nextLine().equalsIgnoreCase("s");
        }

        System.out.print("\n¿Desea reportar algún voto como fraudulento? (s/n): ");
        if (scanner.nextLine().equalsIgnoreCase("s")) {
            System.out.print("Ingrese el ID del candidato afectado: ");
            int candidatoId = scanner.nextInt();
            System.out.print("Ingrese el ID del voto fraudulento: ");
            int votoId = scanner.nextInt();
            scanner.nextLine();

            Candidato candidatoReportado = null;
            for (Candidato c : urna.listaCandidatos) {
                if (c.getIdCandidato() == candidatoId) {
                    candidatoReportado = c;
                    break;
                }
            }

            if (candidatoReportado != null) {
                if (urna.reportarVoto(candidatoReportado, votoId)) {
                    System.out.println("Voto reportado como fraudulento.");
                } else {
                    System.out.println("No se encontró el voto especificado.");
                }
            } else {
                System.out.println("Candidato no encontrado.");
            }
        }

        System.out.println("\n--- Resultados de la Votación ---");
        Map<String, Integer> resultados = urna.obtenerResultados();
        for (Map.Entry<String, Integer> entry : resultados.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue() + " votos");
        }

        scanner.close();
    }
}