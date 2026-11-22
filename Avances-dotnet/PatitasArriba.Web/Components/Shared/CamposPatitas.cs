using Microsoft.AspNetCore.Components;
using Microsoft.AspNetCore.Components.Forms;
using Microsoft.AspNetCore.Components.Web;

namespace PatitasArriba.Web.Components.Shared {
    // Campos de formulario de Patitas Arriba. Funcionan igual que los de Blazor (InputText, InputNumber, ...),
    // pero validan el campo al salir de él, aunque no se haya escrito nada: así el error en rojo aparece
    // apenas se hace clic y se sale, igual que en el login y las pantallas de acceso.
    // Se usan igual: <PaInputText class="form-control" @bind-Value="ViewModel.Nombre" />
    public static class ValidacionAlSalir {
        // Agrega el evento onblur a los atributos que ya trae el campo (class, tabindex, etc.)
        public static IReadOnlyDictionary<string, object> Agregar(IReadOnlyDictionary<string, object>? atributos, EventCallback<FocusEventArgs> alSalir) {
            Dictionary<string, object> resultado = atributos == null ? [] : new Dictionary<string, object>(atributos);
            resultado["onblur"] = alSalir;
            return resultado;
        }

        public static void Validar(EditContext? contexto, FieldIdentifier campo) {
            if (contexto != null) {
                contexto.NotifyFieldChanged(campo);
            }
        }
    }

    public class PaInputText : InputText {
        protected override void OnParametersSet() {
            base.OnParametersSet();
            AdditionalAttributes = ValidacionAlSalir.Agregar(AdditionalAttributes,
                EventCallback.Factory.Create<FocusEventArgs>(this, () => ValidacionAlSalir.Validar(EditContext, FieldIdentifier)));
        }
    }

    public class PaInputTextArea : InputTextArea {
        protected override void OnParametersSet() {
            base.OnParametersSet();
            AdditionalAttributes = ValidacionAlSalir.Agregar(AdditionalAttributes,
                EventCallback.Factory.Create<FocusEventArgs>(this, () => ValidacionAlSalir.Validar(EditContext, FieldIdentifier)));
        }
    }

    public class PaInputNumber<TValue> : InputNumber<TValue> {
        protected override void OnParametersSet() {
            base.OnParametersSet();
            // Si se borra el número o se escribe algo que no es número, Blazor muestra por defecto
            // "The PrecioBase field must be a number." (en inglés y con el nombre interno de la propiedad)
            if (ParsingErrorMessage.StartsWith("The ", StringComparison.Ordinal)) {
                ParsingErrorMessage = "Ingrese un número válido";
            }
            AdditionalAttributes = ValidacionAlSalir.Agregar(AdditionalAttributes,
                EventCallback.Factory.Create<FocusEventArgs>(this, () => ValidacionAlSalir.Validar(EditContext, FieldIdentifier)));
        }
    }

    public class PaInputSelect<TValue> : InputSelect<TValue> {
        protected override void OnParametersSet() {
            base.OnParametersSet();
            AdditionalAttributes = ValidacionAlSalir.Agregar(AdditionalAttributes,
                EventCallback.Factory.Create<FocusEventArgs>(this, () => ValidacionAlSalir.Validar(EditContext, FieldIdentifier)));
        }
    }

    public class PaInputDate<TValue> : InputDate<TValue> {
        protected override void OnParametersSet() {
            base.OnParametersSet();
            // Igual que en PaInputNumber: el mensaje por defecto es "The HoraInicio field must be a time."
            if (ParsingErrorMessage.StartsWith("The ", StringComparison.Ordinal)) {
                ParsingErrorMessage = Type switch {
                    InputDateType.Time => "Ingrese una hora válida",
                    InputDateType.DateTimeLocal => "Ingrese una fecha y hora válidas",
                    InputDateType.Month => "Ingrese un mes válido",
                    _ => "Ingrese una fecha válida"
                };
            }
            AdditionalAttributes = ValidacionAlSalir.Agregar(AdditionalAttributes,
                EventCallback.Factory.Create<FocusEventArgs>(this, () => ValidacionAlSalir.Validar(EditContext, FieldIdentifier)));
        }
    }
}
