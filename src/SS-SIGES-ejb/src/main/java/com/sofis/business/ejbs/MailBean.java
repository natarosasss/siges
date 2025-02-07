package com.sofis.business.ejbs;

import com.sofis.business.interceptors.LoggedInterceptor;
import com.sofis.business.properties.LabelsEJB;
import com.sofis.business.utils.MailsTemplateUtils;
import com.sofis.business.utils.ProgProyUtils;
import com.sofis.entities.codigueras.ConfiguracionCodigos;
import com.sofis.entities.constantes.ConstanteApp;
import com.sofis.entities.constantes.ConstantesEstandares;
import com.sofis.entities.constantes.MailVariables;
import com.sofis.entities.constantes.MensajesNegocio;
import com.sofis.entities.data.Configuracion;
import com.sofis.entities.data.Estados;
import com.sofis.entities.data.MailsTemplate;
import com.sofis.entities.data.Organismos;
import com.sofis.entities.data.Programas;
import com.sofis.entities.data.Proyectos;
import com.sofis.entities.data.SsUsuario;
import com.sofis.exceptions.GeneralException;
import com.sofis.exceptions.MailException;
import com.sofis.generico.utils.generalutils.EmailValidator;
import com.sofis.generico.utils.generalutils.StringsUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptors;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author Usuario
 */
@Named
@Stateless(name = "MailBean")
@LocalBean
@Interceptors({LoggedInterceptor.class})
public class MailBean {

	private static final Logger logger = Logger.getLogger(MailBean.class.getName());

	private static final Semaphore MUTEX = new Semaphore(10);

	@Inject
	private ConfiguracionBean cnfBean;
	@Inject
	private MailsTemplateBean mailsTemplateBean;
	@Inject
	private OrganismoBean organismoBean;
	@Inject
	private ConfiguracionBean configuracionBean;
	@EJB
	private SsUsuarioBean ssUsuarioBean;

	private Session mailSession = null;

	public MailBean() {
	}

	private boolean conCorreo(Integer orgPk) {
		boolean conCorreo = true;

		if (orgPk != null) {
			try {
				Configuracion cnfCorreo = cnfBean.obtenerCnfPorCodigoYOrg(ConstantesEstandares.CON_CORREO, orgPk);
				conCorreo = Boolean.parseBoolean(cnfCorreo.getCnfValor());
			} catch (Exception ex) {
				logger.log(Level.WARNING, "Error al obtener la configuracion 'CON_CORREO'.");
//                conCorreo = false;
			}
		}
		return conCorreo;
	}

	public boolean enviarMail(String subject, String from, String[] recipients, String message, Integer orgPk) {
		return enviarMail(subject, from, null, null, recipients, message, orgPk);
	}

	public boolean enviarMail(String subject, String from, String[] recipientsTO, String[] recipientsCC, String[] recipientsBCC, String message, Integer orgPk) {
		return enviarMail(subject, from, recipientsTO, recipientsCC, recipientsBCC, message, null, null, orgPk);
	}

	public boolean enviarMail(String subject, String from, String[] recipientsTO, String[] recipientsCC, String[] recipientsBCC, String message, String fileName, byte[] fileArr, Integer orgPk) {

		if (conCorreo(orgPk)) {

			/**
			 * BRUNO 03-06-17: Bug de memoria. Inicializaba el InitialContext
			 * pero nunca lo cerraba. Se cambia para cerrarlo en el "finally" si
			 * es distinto de null.
			 */
			InitialContext ic = null;

			try {
				ic = new InitialContext();
				mailSession = (Session) ic.lookup(ConstanteApp.JNDI_MAIL);


				Configuracion cnfDebug = cnfBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.MAIL_DEBUG, orgPk);
				
                                if (cnfDebug != null && cnfDebug.getCnfValor() != null && cnfDebug.getCnfValor().equalsIgnoreCase("true")) {
					mailSession.getProperties().put("mail.smtp.debug", true);
					mailSession.setDebug(true);
                                }

                                Configuracion cnfTls = cnfBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.MAIL_TLS, orgPk);
                                if (cnfTls != null && cnfTls.getCnfValor() != null && cnfTls.getCnfValor().equalsIgnoreCase("true")) {
                                        mailSession.getProperties().put("mail.smtp.starttls.enable", true);
                                        mailSession.getProperties().put("mail.smtp.ssl.trust", "*");
                                }                                
                                
                                
                                /*
                                *  11-04-2018  Nico: Se cambia la condición de si esta en debug por un if-else, para agregar la lógica
                                *       solicitada para poder realizar el modo debug.
                                */

				Configuracion cnfDebugActivado = cnfBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.DEBUG, null);
				
                                if (cnfDebugActivado != null && cnfDebugActivado.getCnfValor() != null && cnfDebugActivado.getCnfValor().equals("true")) {

                                        MimeMessage msg = new MimeMessage(mailSession);


                                        /*
                                        *       Se setea el encabezado del mail, incluyendo del subject.
                                        */

                                        msg.setSubject("Mail Debug - SIGES");
                                        if (StringsUtils.isEmpty(from)) {
                                                String fromName = "SIGES";
                                                if (orgPk != null) {
                                                        Configuracion cnf = cnfBean.obtenerCnfPorCodigoYOrg("MAIL_FROM", orgPk);
                                                        InternetAddress addressFrom = new InternetAddress();
                                                        addressFrom.setPersonal(fromName);
                                                        addressFrom.setAddress(cnf.getCnfValor());
                                                        msg.setFrom(addressFrom);
                                                } else {
                                                        msg.setFrom();
                                                }

                                        } else {
                                                InternetAddress addressFrom = new InternetAddress();
                                                addressFrom.setPersonal(from);
                                                addressFrom.setAddress(from);
                                                msg.setFrom(addressFrom);
                                        }


                                        /*
                                        *    Se carga la configuración de dirección de destino.
                                        *   En caso de que la configuración no se encuentre o la dirección sea invalida se cancela la operación.
                                        */                                    

                                        Configuracion cnfCasillaDestino = cnfBean.obtenerCnfPorCodigoYOrg(ConfiguracionCodigos.DEBUG_DESTINO, null);
                                        if(cnfCasillaDestino != null && validarEMail(cnfCasillaDestino.getCnfValor())){
                                            InternetAddress[] addressTO = new InternetAddress[1];
                                            addressTO[0] = new InternetAddress(cnfCasillaDestino.getCnfValor());
                                            msg.setRecipients(Message.RecipientType.TO, addressTO);
                                        }else{
                                            logger.log(Level.SEVERE, "No se encontró la configuración con la dirección de destino, o la dirección encontrada es inválida");  
                                            return false;
                                        }

                                        String encoding = "utf8";

                                        try {
                                            Configuracion cnfEncoding = cnfBean.obtenerCnfPorCodigoYOrg("MAIL_ENCODING", orgPk);
                                            if (cnfEncoding != null) {
                                                    encoding = cnfEncoding.getCnfValor();
                                            }
                                        } catch (GeneralException ge) {
                                                logger.log(Level.WARNING, "No se pudo obtener \"MAIL_ENCODING\" de las configuraciones. Se toma por defecto: " + encoding);
                                        }

                                        /* 
                                        *    Acá tiene que ir setteando el cuerpo del correo como se pide. Para poner todas las direcciones de destino que es lo que se pide,
                                        *   tengo que recorrer todas las listas de direcciones de destino pasadas por parámetro.
                                        */

                                        String messageDebug = "Subject: " + subject + " \n\n";

                                        messageDebug = messageDebug + "Email to: "; 

                                        if(recipientsTO != null){
                                            for(String iterRecipTO : recipientsTO){
                                                SsUsuario usuario = ssUsuarioBean.obtenerSsUsuarioPorMail(iterRecipTO);
                                                boolean isOfiActivo = orgPk == null || (usuario != null && usuario.isSsUsuOfiActivo(orgPk));

                                                if (validarEMail(iterRecipTO) && (usuario != null && usuario.getUsuVigente()) && isOfiActivo) {
                                                    messageDebug = messageDebug + iterRecipTO + ", ";
                                                }else{
                                                    logger.log(Level.WARNING, "Se descarta la dirección de mail: '" + iterRecipTO + "'. La dirección no es correcta o el usuario no está activo.");                                            
                                                }
                                            }
                                        }

                                        if(recipientsCC != null){                                    
                                            for(String iterRecipCC : recipientsCC){
                                                SsUsuario usuario = ssUsuarioBean.obtenerSsUsuarioPorMail(iterRecipCC);
                                                boolean isOfiActivo = orgPk == null || (usuario != null && usuario.isSsUsuOfiActivo(orgPk));

                                                if (validarEMail(iterRecipCC) && (usuario != null && usuario.getUsuVigente()) && isOfiActivo) {
                                                    messageDebug = messageDebug + iterRecipCC + ", ";
                                                }else{
                                                    logger.log(Level.WARNING, "Se descarta la dirección de mail: '" + iterRecipCC + "'. La dirección no es correcta o el usuario no está activo.");                                            
                                                }
                                            }
                                        }

                                        if(recipientsBCC != null){                                    
                                            for(String iterRecipBCC : recipientsBCC){
                                                SsUsuario usuario = ssUsuarioBean.obtenerSsUsuarioPorMail(iterRecipBCC);
                                                boolean isOfiActivo = orgPk == null || (usuario != null && usuario.isSsUsuOfiActivo(orgPk));

                                                if (validarEMail(iterRecipBCC) && (usuario != null && usuario.getUsuVigente()) && isOfiActivo) {
                                                    messageDebug = messageDebug + iterRecipBCC + ", ";
                                                }else{
                                                    logger.log(Level.WARNING, "Se descarta la dirección de mail: '" + iterRecipBCC + "'. La dirección no es correcta o el usuario no está activo.");                                            
                                                }
                                            }
                                        }                    

                                        /*
                                        *   Esta parte esta hecha para sacar el ", " que queda al final.
                                        */
                                        char[] finalMensaje = new char[2];
                                        messageDebug.getChars(messageDebug.length()-2, messageDebug.length()-1, finalMensaje, 0);

                                        if(finalMensaje[0] == ','){
                                           messageDebug = messageDebug.subSequence(0, messageDebug.length()-2) + "";
                                           messageDebug = messageDebug + "\n\n";
                                        }

                                        messageDebug = messageDebug + message;

                                        /*
                                        *   Seteo el mensaje-
                                        */
                                        msg.setText(messageDebug, encoding);

                                        msg.setHeader("Content-Type", "text/html; charset=\"" + encoding + "\"");
                                        msg.setHeader("Content-Transfer-Encoding", "quoted-printable");

                                        msg.saveChanges();

                                        EnviarMailThread emt = new EnviarMailThread(msg);
                                        emt.start();
                                
				}else{
                                    
                                        /*
                                        *  11-04-2018 Nico: Este código se ejecuta cuando no se encuetra en modo debug
                                        */

                                        boolean hasRecipient = false;

                                        MimeMessage msg = new MimeMessage(mailSession);

                                        msg.setSubject(subject);
                                        if (StringsUtils.isEmpty(from)) {
                                                String fromName = "SIGES";
                                                if (orgPk != null) {
                                                        Configuracion cnf = cnfBean.obtenerCnfPorCodigoYOrg("MAIL_FROM", orgPk);
                                                        InternetAddress addressFrom = new InternetAddress();
                                                        addressFrom.setPersonal(fromName);
                                                        addressFrom.setAddress(cnf.getCnfValor());
                                                        msg.setFrom(addressFrom);
                                                } else {
                                                        msg.setFrom();
                                                }

                                        } else {
                                                InternetAddress addressFrom = new InternetAddress();
                                                addressFrom.setPersonal(from);
                                                addressFrom.setAddress(from);
                                                msg.setFrom(addressFrom);
                                        }

                                        List<InternetAddress> listAddress = null;
                                        if (recipientsTO != null) {
                                                listAddress = new ArrayList<>();
                                                loadRecipients(listAddress, recipientsTO, orgPk);

                                                InternetAddress[] addressTO = listAddress.toArray(new InternetAddress[listAddress.size()]);
                                                msg.setRecipients(Message.RecipientType.TO, addressTO);
                                                if (addressTO != null && addressTO.length > 0) {
                                                        hasRecipient = true;
                                                }
                                        }
                                        if (recipientsCC != null) {
                                                listAddress = new ArrayList<>();
                                                loadRecipients(listAddress, recipientsCC, orgPk);

                                                InternetAddress[] addressCC = listAddress.toArray(new InternetAddress[listAddress.size()]);
                                                msg.setRecipients(Message.RecipientType.CC, addressCC);
                                                if (addressCC != null && addressCC.length > 0) {
                                                        hasRecipient = true;
                                                }
                                        }
                                        if (recipientsBCC != null) {
                                                listAddress = new ArrayList<>();
                                                loadRecipients(listAddress, recipientsBCC, orgPk);

                                                InternetAddress[] addressBCC = listAddress.toArray(new InternetAddress[listAddress.size()]);
                                                msg.setRecipients(Message.RecipientType.BCC, addressBCC);
                                                if (addressBCC != null && addressBCC.length > 0) {
                                                        hasRecipient = true;
                                                }
                                        }

                                        if (!hasRecipient) {
                                                MailException me = new MailException();
                                                me.addError(MensajesNegocio.ERROR_MAIL_RECIPIENT);
                                                throw me;
                                        }

                                        String encoding = "utf8";

                                        try {
                                                Configuracion cnfEncoding = cnfBean.obtenerCnfPorCodigoYOrg("MAIL_ENCODING", orgPk);
                                                if (cnfEncoding != null) {
                                                        encoding = cnfEncoding.getCnfValor();
                                                }
                                        } catch (GeneralException ge) {
                                                logger.log(Level.WARNING, "No se pudo obtener \"MAIL_ENCODING\" de las configuraciones. Se toma por defecto: " + encoding);
                                        }

                                        msg.setText(message, encoding);

                                        msg.setHeader("Content-Type", "text/html; charset=\"" + encoding + "\"");
                                        msg.setHeader("Content-Transfer-Encoding", "quoted-printable");

                                        // Attachment ----------------------------------------------------------------
                                        if (fileArr != null && fileArr.length > 0) {
											// Create the message part
											BodyPart messageBodyPart = new MimeBodyPart();
											messageBodyPart.setText("Text MimeBodyPart");

											// Now set the actual message
											// messageBodyPart.setText("This is message body");
											// Create a multipar message
											Multipart multipart = new MimeMultipart();

											MimeBodyPart mbp = new MimeBodyPart();
											String fName = !StringsUtils.isEmpty(fileName) ? fileName : LabelsEJB.getValue("mail_file_name", orgPk);
											mbp.setFileName(fName);
											DataSource ds = new ByteArrayDataSource(fileArr, "application/octet-stream");
											mbp.setDataHandler(new DataHandler(ds));
											multipart.addBodyPart(mbp);

											msg.setContent(multipart);

                                        }
                                        // Attachment fin ----------------------------------------------------------------

                                        msg.saveChanges();

                                         //  Transport.send(msg);
                                        EnviarMailThread emt = new EnviarMailThread(msg);
                                        emt.start();

                                }
                                
                                return true;
                                    
			} catch (MailException me) {
				logger.log(Level.WARNING, "Error al enviar el msg: \"" + message + "\"");
				logger.log(Level.WARNING, "Una de las siguientes direcciones falla: "
						+ (recipientsTO != null ? "(TO:" + recipientsTO.length + ")" + StringsUtils.concat(recipientsTO) : "") + ","
						+ (recipientsCC != null ? "(CC:" + recipientsCC.length + ")" + StringsUtils.concat(recipientsCC) : "") + ","
						+ (recipientsBCC != null ? "(BCC:" + recipientsBCC.length + ")" + StringsUtils.concat(recipientsBCC) : "") + "\"");
				me.addError(MensajesNegocio.ERROR_MAIL_ENVIO);
				throw me;
			} catch (Exception ex) {
				logger.log(Level.WARNING, null, ex);
				MailException me = new MailException();
				me.addError(MensajesNegocio.ERROR_MAIL_ENVIO);
				throw me;

			} finally {
				if (ic != null) {
					try {
						ic.close();
					} catch (NamingException ex) {
						Logger.getLogger(MailBean.class.getName()).log(Level.SEVERE, null, ex);
					}
				}
			}

		} else {
			String to = (recipientsTO != null ? "(TO:" + recipientsTO.length + ")" + StringsUtils.concat(recipientsTO) : "") + ","
					+ (recipientsCC != null ? "(CC:" + recipientsCC.length + ")" + StringsUtils.concat(recipientsCC) : "") + ","
					+ (recipientsBCC != null ? "(BCC:" + recipientsBCC.length + ")" + StringsUtils.concat(recipientsBCC) : "");
			logger.log(Level.INFO, "[Mail deshabilitado] No se envia correo: {0}, mensaje: {1}, " + to, new Object[]{subject, message});
//			MailException me = new MailException();
//			me.addError(MensajesNegocio.ERROR_MAIL_DISABLED);
//			throw me;
                        return false ;
		}
	}

	private List<InternetAddress> loadRecipients(List<InternetAddress> listAddress, String[] recipients, Integer orgPk) throws AddressException {
		if (listAddress == null) {
			listAddress = new ArrayList<>();
		}
		for (String recipient : recipients) {
			SsUsuario usuario = ssUsuarioBean.obtenerSsUsuarioPorMail(recipient);
			boolean isOfiActivo = orgPk == null || (usuario != null && usuario.isSsUsuOfiActivo(orgPk));
                
			if (validarEMail(recipient) && (usuario != null && usuario.getUsuVigente()) && isOfiActivo) {
                            listAddress.add(new InternetAddress(recipient));
                            
                            /*
                            * 10-04-18 Nico: Deje lo de abajo comentado y puse la línea de arriba para que siga mandando mails cuando alguno de los
                            *           destinatarios tiene un mail invalido, o es un usuario no vigente.
                            */
                                /*try {
					listAddress.add(new InternetAddress(recipient));
				} catch (AddressException ex) {
					logger.log(Level.SEVERE, null, ex);
				}*/
			} else {
				logger.log(Level.WARNING, "Se descarta la dirección de mail: '" + recipient + "'. La dirección no es correcta o el usuario no está activo.");
			}
		}
		return listAddress;
	}

	/**
	 * Valida el mail aportado.
	 *
	 * @param email
	 * @return
	 */
	public boolean validarEMail(String email) {
		return new EmailValidator().validate(email);
	}

	/**
	 * Envía un comunicado informando la solicitud de una aprobacion para un
	 * Programa o Proyecto.
	 *
	 * @param orgPk
	 * @param destinatario
	 */
	public void comunicarSolicitudAprobacion(Integer orgPk, Object obj, String... destinatario) throws GeneralException {
		MailsTemplate mt = mailsTemplateBean.obtenerMailTmpPorCodigo(MailsTemplateBean.MAIL_SOL_APROBACION, orgPk);

		if (conCorreo(orgPk) && mt != null) {
			String asunto = mt.getMailTmpAsunto();
			String mensaje = mt.getMailTmpMensaje();

			Organismos org = organismoBean.obtenerOrgPorId(orgPk, false);
			String urlSistema = configuracionBean.obtenerCnfValorPorCodigo(ConfiguracionCodigos.URL_SISTEMA, null); 

			String tipo = "";
			Integer fichaPk = null;
			String nombre = "";
			String url = "";

			if (obj instanceof Programas) {
				Programas p = (Programas) obj;
				tipo = "programa";
				fichaPk = p.getProgPk();
				nombre = p.getProgNombre();
				url = ProgProyUtils.obtenerURL(urlSistema, p);
			
			} else if (obj instanceof Proyectos) {
				Proyectos p = (Proyectos) obj;
				tipo = "proyecto";
				fichaPk = p.getProyPk();
				nombre = p.getProyNombre();
				url = ProgProyUtils.obtenerURL(urlSistema, p);
			}
			
			Map<String, String> valores = new HashMap<>();
			valores.put(MailVariables.TIPO_PROG_PROY, tipo);
			valores.put(MailVariables.ID_PROG_PROY, fichaPk.toString());
			valores.put(MailVariables.NOMBRE_PROG_PROY, nombre);
			valores.put(MailVariables.ORGANISMO_NOMBRE, org.getOrgNombre());
			valores.put(MailVariables.ORGANISMO_DIRECCION, org.getOrgDireccion());
			valores.put(MailVariables.URL_SISTEMA, urlSistema); 
			valores.put(MailVariables.URL_PROYECTO, url); 
			mensaje = MailsTemplateUtils.instanciarConHashMap(mensaje, valores);

			this.enviarMail(asunto, "", destinatario, mensaje, orgPk);
		}
	}

	/**
	 * Envía un comunicado informando el cambio de estado para un Programa o
	 * Proyecto.
	 *
	 * @param orgPk
	 * @param obj Programa o Proyecto
	 * @param destinatario
	 * @throws GeneralException
	 */
	public void comunicarCambioEstado(Integer orgPk, Object obj, String... destinatario) throws GeneralException {
		MailsTemplate mt = mailsTemplateBean.obtenerMailTmpPorCodigo(MailsTemplateBean.MAIL_CAMBIO_ESTADO, orgPk);

		if (conCorreo(orgPk) && mt != null) {
			String asunto = mt.getMailTmpAsunto();
			String mensaje = mt.getMailTmpMensaje();

			Organismos org = organismoBean.obtenerOrgPorId(orgPk, false);
			String urlSistema = configuracionBean.obtenerCnfValorPorCodigo(ConfiguracionCodigos.URL_SISTEMA, null); 
			
			String tipo = "";
			Integer fichaPk = null;
			String nombre = "";
			Estados estado = null;
			String url = "";
			if (obj instanceof Programas) {
				Programas p = (Programas) obj;
				tipo = LabelsEJB.getValue("programa", orgPk);
				fichaPk = p.getProgPk();
				nombre = p.getProgNombre();
				estado = p.getProgEstFk();
				url = ProgProyUtils.obtenerURL(urlSistema, p);

			} else if (obj instanceof Proyectos) {
				Proyectos p = (Proyectos) obj;
				tipo = LabelsEJB.getValue("proyecto", orgPk);
				fichaPk = p.getProyPk();
				nombre = p.getProyNombre();
				estado = p.getProyEstFk();
				url = ProgProyUtils.obtenerURL(urlSistema, p);
			}

			Map<String, String> valores = new HashMap<>();
			valores.put(MailVariables.TIPO_PROG_PROY, tipo);
			valores.put(MailVariables.ID_PROG_PROY, fichaPk.toString());
			valores.put(MailVariables.NOMBRE_PROG_PROY, nombre);
			valores.put(MailVariables.FASE_PROG_PROY, LabelsEJB.getValue("estado_" + estado.getEstPk(), orgPk));
			valores.put(MailVariables.ORGANISMO_NOMBRE, org.getOrgNombre());
			valores.put(MailVariables.ORGANISMO_DIRECCION, org.getOrgDireccion());
			valores.put(MailVariables.URL_SISTEMA, urlSistema); 
			valores.put(MailVariables.URL_PROYECTO, url); 

			mensaje = MailsTemplateUtils.instanciarConHashMap(mensaje, valores);

			this.enviarMail(asunto, "", destinatario, mensaje, orgPk);
		}
	}

	/**
	 * Envía un comunicado informando que queda pendiente de aprobación.
	 *
	 * @param orgPk
	 * @param obj Programa o Proyecto
	 * @param destinatario
	 * @throws GeneralException
	 */
	public void comunicarProgProyPendiente(Integer orgPk, Object obj, String... destinatario) throws MailException {
		if (orgPk != null && destinatario != null) {
			MailsTemplate mt = mailsTemplateBean.obtenerMailTmpPorCodigo(MailsTemplateBean.MAIL_PROG_PROY_PENDIENTE, orgPk);

			if (conCorreo(orgPk) && mt != null) {
				String asunto = mt.getMailTmpAsunto();
				String mensaje = mt.getMailTmpMensaje();

				Organismos org = organismoBean.obtenerOrgPorId(orgPk, false);
				String urlSistema = configuracionBean.obtenerCnfValorPorCodigo(ConfiguracionCodigos.URL_SISTEMA, null); 

				String tipo = "";
				Integer fichaPk = null;
				String nombre = "";
				String url = "";
				
				if (obj instanceof Programas) {
					Programas p = (Programas) obj;
					tipo = LabelsEJB.getValue("programa", orgPk);
					fichaPk = p.getProgPk();
					nombre = p.getProgNombre();
					url = ProgProyUtils.obtenerURL(urlSistema, p);

				} else if (obj instanceof Proyectos) {
					Proyectos p = (Proyectos) obj;
					tipo = LabelsEJB.getValue("proyecto", orgPk);
					fichaPk = p.getProyPk();
					nombre = p.getProyNombre();
					url = ProgProyUtils.obtenerURL(urlSistema, p);
				}

				Map<String, String> valores = new HashMap<>();
				valores.put(MailVariables.TIPO_PROG_PROY, tipo);
				valores.put(MailVariables.ID_PROG_PROY, fichaPk.toString());
				valores.put(MailVariables.NOMBRE_PROG_PROY, nombre);
				valores.put(MailVariables.ORGANISMO_NOMBRE, org.getOrgNombre());
				valores.put(MailVariables.ORGANISMO_DIRECCION, org.getOrgDireccion());
				valores.put(MailVariables.URL_SISTEMA, urlSistema); 
				valores.put(MailVariables.URL_PROYECTO, url); 

				mensaje = MailsTemplateUtils.instanciarConHashMap(mensaje, valores);

				this.enviarMail(asunto, "", null, null, destinatario, mensaje, orgPk);
			}
		}
	}

	/**
	 * Envía un mail con la nueva constraseña generada para el usuario
	 *
	 * @param orgPk
	 * @param nombre
	 * @param contrasenia
	 * @param email
	 * @return boolean
	 * @throws GeneralException
	 */
	public boolean comunicarNuevaContrasenia(Integer orgPk, String nombre, String contrasenia, String email) throws GeneralException {
		MailsTemplate mt = mailsTemplateBean.obtenerMailTmpPorCodigo(MailsTemplateBean.MAIL_CAMBIO_CONTRASENIA, orgPk);
		if (mt != null) {
			String asunto = mt.getMailTmpAsunto();
			String mensaje = mt.getMailTmpMensaje();

			Map<String, String> valores = new HashMap<>();
			valores.put(MailVariables.NOMBRE, nombre);
			valores.put(MailVariables.CONTRASENIA, contrasenia);
			if (orgPk != null) {
				Organismos org = organismoBean.obtenerOrgPorId(orgPk, false);
				valores.put(MailVariables.ORGANISMO_NOMBRE, org.getOrgNombre());
				valores.put(MailVariables.ORGANISMO_DIRECCION, org.getOrgDireccion());
			}

			String urlSistema = configuracionBean.obtenerCnfValorPorCodigo(ConfiguracionCodigos.URL_SISTEMA, null);
			valores.put(MailVariables.URL_SISTEMA, urlSistema);

			mensaje = MailsTemplateUtils.instanciarConHashMap(mensaje, valores);

			String[] dests = new String[1];
			dests[0] = email;

			return this.enviarMail(asunto, "", dests, mensaje, orgPk);
		}
		return false;
	}

	public void comunicarNuevoUsuario(Integer orgPk, String mail, String clave) {
		MailsTemplate mt = mailsTemplateBean.obtenerMailTmpPorCodigo(MailsTemplateBean.MAIL_NVO_USUARIO, orgPk);

		if (mt != null) {
			String asunto = mt.getMailTmpAsunto();
			String mensaje = mt.getMailTmpMensaje();

			Map<String, String> valores = new HashMap<>();
			valores.put(MailVariables.USU_MAIL, mail);
			valores.put(MailVariables.USU_PASSWORD, clave);

			Organismos org = organismoBean.obtenerOrgPorId(orgPk, false);
			valores.put(MailVariables.ORGANISMO_NOMBRE, org.getOrgNombre());
			valores.put(MailVariables.ORGANISMO_DIRECCION, org.getOrgDireccion());
			
			String urlSistema = configuracionBean.obtenerCnfValorPorCodigo(ConfiguracionCodigos.URL_SISTEMA, null);
			valores.put(MailVariables.URL_SISTEMA, urlSistema);

			mensaje = MailsTemplateUtils.instanciarConHashMap(mensaje, valores);

			String[] dests = new String[1];
			dests[0] = mail;

			this.enviarMail(asunto, "", dests, mensaje, orgPk);
		}
	}

	private class EnviarMailThread extends Thread {

		Message msg = null;

		public EnviarMailThread(Message msg) {
			this.msg = msg;
		}

		@Override
		public void run() {

			try {
				MUTEX.acquire();
				Transport.send(msg);
				logger.log(Level.INFO, "## Mail enviado correctamente.");
			} catch (Exception ex) {
				logger.log(Level.INFO, "## El mail no pudo ser enviado.");
				logger.log(Level.SEVERE, null, ex);
			} finally {
				MUTEX.release();
			}
		}
	}
}
