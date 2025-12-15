import { useEffect, useState } from "react";
import { createPortal } from "react-dom";

const Portal = ({ children }) => {
    const [container] = useState(() => document.createElement("div"));

    useEffect(() => {
        document.body.appendChild(container);
        // Prevent scrolling on mount
        document.body.style.overflow = "hidden";

        return () => {
            document.body.removeChild(container);
            // Re-enable scrolling on unmount
            document.body.style.overflow = "unset";
        };
    }, [container]);

    return createPortal(children, container);
};

export default Portal;
