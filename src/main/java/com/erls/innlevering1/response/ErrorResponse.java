package com.erls.innlevering1.response;

public class ErrorResponse extends AbstractReponse {

    public class Error {

        private Object error;

        public Error(Object error) {
            this.error = error;
        }

        public Object getError() {
            return this.error;
        }

    }

    private Error response;

    public ErrorResponse(Object error) {
        response = new Error(error);
    }

    public Error getResponse() {
        return this.response;
    }
}
