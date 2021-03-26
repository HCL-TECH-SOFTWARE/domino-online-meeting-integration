package com.hcl.labs.domi.tools;

public class DOMIException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @param message - what went wrong, with formatting
   */
  public DOMIException(final String message) {
    super(String.format(String.valueOf(message)));
  }

  /**
   * @param message - what went wrong
   * @param args Arguments that are passed into the format function
   */
  public DOMIException(final String message, final Object... args) {
    super(String.format(String.valueOf(message), args));
  }

  /**
   * @param cause as a throwable
   */
  public DOMIException(final Throwable cause) {
    super(cause);
  }

}
