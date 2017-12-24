package codes;

public class ResponseCodes {
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int METHOD_NOT_ALLOWED = 405;
}

//    Если токен, присланный пользователем в запросе, неизвестен серверу: HTTP 403.
//    Если токен в запросе отсутствует: HTTP 401.
//    Если запрос не соответствует формату, который ожидался по данному адресу (для этого ресурса): HTTP 400.
//    Если в запросе указан метод, который не поддерживается по данному адресу (для этого ресурса): HTTP 405.
//    В целях отладки при обработке запросов предлагается сообщения об исключительных ситуациях (exceptions) отправлять клиенту в качестве ответа с кодом состояния HTTP 500.
//Если после переподключения сервер выдаёт статус 403, то клиент должен заново аутентифицироваться.
