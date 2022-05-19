package Models;

public class FirebaseMessage {
    private String contenido;

    public String getReceptorMail() {
        return receptorMail;
    }

    public void setReceptorMail(String receptorMail) {
        this.receptorMail = receptorMail;
    }

    private String receptorMail;

    public String getRemitenteNombre() {
        return remitenteNombre;
    }

    public void setRemitenteNombre(String remitenteNombre) {
        this.remitenteNombre = remitenteNombre;
    }

    public String getRemitenteMail() {
        return remitenteMail;
    }

    public void setRemitenteMail(String remitenteMail) {
        this.remitenteMail = remitenteMail;
    }

    public String getRemitenteImagen() {
        return remitenteImagen;
    }

    public void setRemitenteImagen(String remitenteImagen) {
        this.remitenteImagen = remitenteImagen;
    }

    private String remitenteImagen;
    private String remitenteMail;
    private String remitenteNombre;

    public FirebaseMessage(String contenido, String remitenteImagen, String remitenteMail, String remitenteNombre, String receptorMail){
        this.contenido=contenido;
        this.remitenteImagen=remitenteImagen;
        this.remitenteMail=remitenteMail;
        this.receptorMail=receptorMail;
        this.remitenteNombre=remitenteNombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }
}
