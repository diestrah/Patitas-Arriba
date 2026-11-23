namespace PatitasArriba.Web.Servicios {
    public interface IRegistroService<T> where T : class {
        Task<List<T>> FindAll();
        Task<T?> FindById(int Id);
        Task Insert(T Entidad);
        Task Update(T Entidad);
        Task Delete(int Id);
    }
}
