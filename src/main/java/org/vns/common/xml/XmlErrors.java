package org.vns.common.xml;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Valery Shyshkin
 */
public class XmlErrors {

    private final List<XmlError> errorList = new ArrayList<>();

    public List<XmlError> getErrorList() {
        return new ArrayList<>(errorList);
    }

    public void addError(XmlError error) {
        if (error == null) {
            return;
        }
        errorList.add(error);
    }

    public void merge(XmlErrors other) {
        this.errorList.addAll(other.getErrorList());
    }

    public boolean isEmpty() {
        return errorList.isEmpty();
    }

    public int size() {
        return errorList.size();
    }

    public void clear() {
        this.errorList.clear();
    }

    public static class XmlResult {

        private String errorCode;
        private boolean warning;

        private List<XmlElement> parentList;

        private String elementClass;
        private boolean defaultClass;
        private XmlElement element;

        public XmlResult(XmlElement element) {
            this.element = element;
            parentList = new ArrayList<>();
        }

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public XmlElement getElement() {
            return element;
        }

        public void setElement(XmlElement element) {
            this.element = element;
        }
        public List<XmlElement> getParentList() {
            return parentList;
        }

        public void setParentList(List<XmlElement> parentList) {
            this.parentList = parentList;
        }

        public String getElementClass() {
            return elementClass;
        }

        public void setElementClass(String elementClass) {
            this.elementClass = elementClass;
        }

        public boolean isDefaultClass() {
            return defaultClass;
        }

        public void setDefaultClass(boolean defaultClass) {
            this.defaultClass = defaultClass;
        }

        public boolean isWarning() {
            return warning;
        }

        public void setWarning(boolean warning) {
            this.warning = warning;
        }

    }

    public static class XmlError {

        private String message;
        private RuntimeException exception;
        private final XmlResult checkResult;

        public XmlError(XmlResult checkResult) {
            this.checkResult = checkResult;
            initError(checkResult);
        }

        private void initError(XmlResult checkResult) {
            String errorCode = checkResult.getErrorCode();
            StringBuilder sb = new StringBuilder();
            if (checkResult.isWarning()) {
                sb.append("Warning. Error code: ").append(errorCode).append(". ");
            } else {
                sb.append("Error. Error code: ").append(errorCode).append(". ");
            }
            sb.append(System.lineSeparator());

            String path = relativePath(checkResult.getParentList(), true);

            sb.append("Search path: '")
                    .append(path)
                    .append("'. ")
                    .append(System.lineSeparator());

            //path = XmlBase.toStringPath(XmlBase.getParentChainList(checkResult.getElement()), true);
            List<XmlElement> list = XmlBase.getParentChainList(checkResult.getElement());
            path = relativePath(list, true);

            sb.append("Element class: ")
                    .append(checkResult.getElement().getClass().getName())
                    .append(".")
                    .append(System.lineSeparator())
                    .append(" Root relative path='")
                    .append(path)
                    .append("'")
                    .append(System.lineSeparator());
            switch (errorCode) {
                case "100":
                    sb.append(" The element should perhaps use the default name of the class:  '")
                            .append(checkResult.getElementClass())
                            .append("'. ")
                            .append(System.lineSeparator());
                    message = sb.toString();
                    exception = new XmlErrors.InvalidClassNameException(message);
                    break;
                
                case "200":
                    sb.append(" Invalid tag name: '")
                            .append(checkResult.getElement().getTagName())
                            .append("'")
                            .append(System.lineSeparator());
                    message = sb.toString();
                    exception = new XmlErrors.InvalidTagNameException(message);
                    break;
                case "210":
                    sb.append(" Invalid class name. Must be :  '")
                            .append(checkResult.getElementClass())
                            .append("'. ")
                            .append(System.lineSeparator());
                    message = sb.toString();
                    exception = new XmlErrors.InvalidClassNameException(message);
                    break;
            }
        }

        protected String relativePath(List<XmlElement> list, boolean includeRoot) {
            String path = "";
            StringBuilder pathBuilder = new StringBuilder();
            String slash = "/";
            int start = includeRoot ? 0 : 1;
            for (int i = start; i < list.size(); i++) {
                if (i == list.size() - 1) {
                    slash = "";
                }
                int idx = -1;
                if (list.get(i).getParent() != null) {
                    idx = list.get(i).getParent().getChilds().indexOf(list.get(i));

                }
                pathBuilder
                        .append(list.get(i).getTagName());
                if (idx >= 0) {
                    pathBuilder.append("[")
                            .append(idx)
                            .append("]");
                }
                pathBuilder.append(slash);
            }
            return pathBuilder.toString();
        }

        public String getMessage() {
            return message;
        }

        public RuntimeException getException() {
            return exception;
        }

        public XmlResult getCheckResult() {
            return checkResult;
        }
        public String getErrorCode() {
            return checkResult.getErrorCode();
        }
        public boolean isWarning() {
            return checkResult.isWarning();
        }

    }//class XmlError

    public static class InvalidClassNameException extends RuntimeException {

        public InvalidClassNameException(String message) {
            super(message);
        }

    }

    public static class InvalidTagNameException extends RuntimeException {

        public InvalidTagNameException(String message) {
            super(message);
        }

    }
}
