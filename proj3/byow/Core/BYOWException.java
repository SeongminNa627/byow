package byow.Core;

/** General exception indicating a Gitlet error.  For fatal errors, the
 *  result of .getMessage() is the error message to be printed.
 *  @author P. N. Hilfinger
 */
class BYOWException extends RuntimeException {


    /** A GitletException with no message. */
    BYOWException() {
        super();
    }

    /** A GitletException MSG as its message. */
    BYOWException(String msg) {
        super(msg);
    }

}
