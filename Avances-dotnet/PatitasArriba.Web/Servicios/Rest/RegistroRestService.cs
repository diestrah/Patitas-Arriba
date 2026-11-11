using System.Net;

namespace PatitasArriba.Web.Servicios.Reste;

public abstract class RegistroRestService<TModel, TDto> : IRegistroService<TModel>
    where TModel : class where TDto : class {
    protected string BaseUrl { get; }
    protected string RecursoUrl { get; }
    protected RegistroRestService(IConfiguration configuration, string recurso) {
        BaseUrl = configuration["RestApiBaseUrl"]?.TrimEnd('/')
            ?? throw new InvalidOperationException("Falta configurar RestApiBaseUrl.");
        if (!Uri.TryCreate(BaseUrl, UriKind.Absolute, out var uri)
            || (uri.Scheme != "http" && uri.Scheme != "https"))
            throw new InvalidOperationException("RestApiBaseUrl debe ser una URL HTTP o HTTPS absoluta.");
        RecursoUrl = $"{BaseUrl}/{recurso}";
    }
    protected abstract TModel ToViewModel(TDto dto);
    protected abstract TDto ToDto(TModel modelo);
    protected abstract int GetId(TModel modelo);
    protected abstract void SetId(TModel modelo, TDto dto);
    protected virtual Task<TModel> ConvertAsync(TDto dto) => Task.FromResult(ToViewModel(dto));
    protected virtual Task PrepareRead() => Task.CompletedTask;
    protected virtual Task BeforeSave(TModel modelo) => Task.CompletedTask;

    public virtual Task<List<TModel>> FindAll() => FindList(RecursoUrl);
    protected async Task<List<TModel>> FindList(string url) {
        var dtos = await HttpUtils.DoGet<List<TDto>>(url);
        await PrepareRead();
        var modelos = new List<TModel>();
        foreach (var dto in dtos) modelos.Add(await ConvertAsync(dto));
        return modelos;
    }
    public virtual async Task<TModel?> FindById(int id) {
        TDto dto;
        try { dto = await HttpUtils.DoGet<TDto>($"{RecursoUrl}/{id}"); }
        catch (HttpRequestException ex) when (ex.StatusCode == HttpStatusCode.NotFound) { return null; }
        await PrepareRead();
        return await ConvertAsync(dto);
    }

    protected async Task<TModel> FindOne(string parametro, string valor) {
        var encontrados = await FindList($"{RecursoUrl}?{parametro}={Uri.EscapeDataString(valor)}");
        return encontrados.FirstOrDefault()
            ?? throw new HttpRequestException("No se encontrÃ³ el registro solicitado.", null, HttpStatusCode.NotFound);
    }
    public virtual async Task Insert(TModel modelo) {
        await BeforeSave(modelo);
        var resultado = await HttpUtils.DoPost<TDto, TDto>(RecursoUrl, ToDto(modelo));
        SetId(modelo, resultado);
    }
    public virtual async Task Update(TModel modelo) {
        await BeforeSave(modelo);
        await HttpUtils.DoPut<TDto, TDto>($"{RecursoUrl}/{GetId(modelo)}", ToDto(modelo));
    }
    public virtual Task Delete(int id) => HttpUtils.DoDelete($"{RecursoUrl}/{id}");
}
