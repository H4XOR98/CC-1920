public class ServiceResult<T> {
    private boolean success;
    private T result;

    public ServiceResult(boolean success, T result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public T getResult() {
        return result;
    }
}

