const imagensInput = document.querySelector('#imagens');
const imagensFeedback = document.querySelector('#imagensFeedback');
const imagemPreviews = document.querySelector('#imagemPreviews');

if (imagensInput && imagensFeedback && imagemPreviews) {
  let urls = [];
  imagensInput.addEventListener('change', () => {
    urls.forEach(URL.revokeObjectURL);
    urls = [];
    imagemPreviews.replaceChildren();
    const arquivos = Array.from(imagensInput.files);
    const tipos = new Set(['image/jpeg', 'image/png', 'image/webp']);
    let erro = '';
    if (arquivos.length > 5) erro = 'Selecione no máximo 5 imagens.';
    else if (arquivos.some(arquivo => !tipos.has(arquivo.type))) erro = 'Use apenas imagens JPEG, PNG ou WebP.';
    else if (arquivos.some(arquivo => arquivo.size > 5_000_000)) erro = 'Cada imagem deve ter até 5 MB.';
    imagensInput.setCustomValidity(erro);
    imagensInput.setAttribute('aria-invalid', erro ? 'true' : 'false');
    imagensFeedback.classList.toggle('field-error', Boolean(erro));
    imagensFeedback.textContent = erro || (arquivos.length ? `${arquivos.length} imagem(ns) selecionada(s).` : '');
    if (erro) return;
    arquivos.forEach((arquivo, indice) => {
      const url = URL.createObjectURL(arquivo);
      urls.push(url);
      const item = document.createElement('figure');
      item.className = 'upload-preview';
      const imagem = document.createElement('img');
      imagem.src = url;
      imagem.alt = `Prévia da imagem ${indice + 1}`;
      const nome = document.createElement('figcaption');
      nome.textContent = arquivo.name;
      item.append(imagem, nome);
      imagemPreviews.append(item);
    });
  });
  window.addEventListener('pagehide', () => urls.forEach(URL.revokeObjectURL));
  imagensInput.form.addEventListener('submit', () => {
    const botao = imagensInput.form.querySelector('button[type="submit"]');
    botao.disabled = true;
    botao.textContent = 'Enviando chamado...';
  });
}
