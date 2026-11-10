const copiarLink = document.querySelector('#copiarLink');
const linkFeedback = document.querySelector('#linkFeedback');

if (copiarLink && linkFeedback) {
  copiarLink.addEventListener('click', async () => {
    try {
      await navigator.clipboard.writeText(`${location.origin}${location.pathname}`);
      linkFeedback.textContent = 'Link copiado. Guarde-o para consultar o chamado.';
    } catch {
      linkFeedback.textContent = 'Copie o endereço desta página na barra do navegador.';
    }
  });
}
