/* Contains function to copy text to clipboard */

function copyToClipboard(text) {
    // Create a temporary textarea element
    const textArea = document.createElement("textarea");
    textArea.value = text;
    
    // Ensure it's not visible but part of the DOM
    textArea.style.position = "fixed";
    textArea.style.left = "-9999px";
    document.body.appendChild(textArea);
    
    // Select and copy
    textArea.select();
    try {
        document.execCommand('copy');
        // Call the parent window's function to show the modal
        if (window.parent && typeof window.parent.showSuccessMessage === 'function') {
            window.parent.showSuccessMessage('linkCopied');
        }
    } catch (err) {
        console.error('Failed to copy text', err);
    }
    
    // Clean up
    document.body.removeChild(textArea);
}
