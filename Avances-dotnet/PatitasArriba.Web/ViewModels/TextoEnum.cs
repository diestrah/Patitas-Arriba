namespace PatitasArriba.Web.ViewModels {
    // Textos amigables para mostrar los enums en pantalla
    public static class TextoEnum {
        public static string De(SexoMascota sexo) {
            return sexo switch {
                SexoMascota.MACHO => "Macho",
                SexoMascota.HEMBRA => "Hembra",
                _ => sexo.ToString()
            };
        }

        public static string De(TipoMascota tipo) {
            return tipo switch {
                TipoMascota.PERRO => "Perro",
                TipoMascota.GATO => "Gato",
                _ => tipo.ToString()
            };
        }

        public static string De(DiaSemana dia) {
            return dia switch {
                DiaSemana.LUNES => "Lunes",
                DiaSemana.MARTES => "Martes",
                DiaSemana.MIERCOLES => "Miércoles",
                DiaSemana.JUEVES => "Jueves",
                DiaSemana.VIERNES => "Viernes",
                DiaSemana.SABADO => "Sábado",
                DiaSemana.DOMINGO => "Domingo",
                _ => dia.ToString()
            };
        }

        public static string De(EstadoCita estado) {
            return estado switch {
                EstadoCita.PENDIENTE => "Pendiente",
                EstadoCita.APROBADO => "Aprobada",
                EstadoCita.RECHAZADO => "Rechazada",
                EstadoCita.CANCELADO => "Cancelada",
                EstadoCita.COMPLETADO => "Completada",
                _ => estado.ToString()
            };
        }

        public static string De(TipoServicio tipo) {
            return tipo switch {
                TipoServicio.CONSULTA_MEDICA => "Consulta médica",
                TipoServicio.OPERACION => "Operación",
                TipoServicio.VACUNACION => "Vacunación",
                TipoServicio.ESTETICA => "Estética",
                TipoServicio.EMERGENCIA => "Emergencia",
                _ => tipo.ToString()
            };
        }

        public static string De(NivelGravedad nivel) {
            return nivel switch {
                NivelGravedad.LEVE => "Leve",
                NivelGravedad.MODERADO => "Moderado",
                NivelGravedad.GRAVE => "Grave",
                NivelGravedad.CRITICO => "Crítico",
                _ => nivel.ToString()
            };
        }

        public static string De(MetodoPago metodo) {
            return metodo switch {
                MetodoPago.EFECTIVO => "Efectivo",
                MetodoPago.TARJETA_DE_CREDITO => "Tarjeta de crédito",
                MetodoPago.TARJETA_DE_DEBITO => "Tarjeta de débito",
                MetodoPago.YAPE => "Yape",
                _ => metodo.ToString()
            };
        }

        public static string De(RolUsuario rol) {
            return rol switch {
                RolUsuario.ADMINISTRADOR => "Administrador",
                RolUsuario.VETERINARIO => "Veterinario",
                RolUsuario.CLIENTE => "Cliente",
                _ => rol.ToString()
            };
        }
    }
}
