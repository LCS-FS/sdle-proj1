function copyHash(hash){
    navigator.clipboard.writeText(hash);
    const button = document.querySelector("#shareHash"+hash)
    button.innerHTML = 'Copied'
}