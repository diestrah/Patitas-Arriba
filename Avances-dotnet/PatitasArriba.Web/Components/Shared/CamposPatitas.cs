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
            AdditionalAttributes = ValidacionAlSalir.Agregar(AdditionalAttributes,
                EventCallback.Factory.Create<FocusEventArgs>(this, () => ValidacionAlSalir.Validar(EditContext, FieldIdentifier)));
        }
    }
}
