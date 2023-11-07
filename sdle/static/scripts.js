function copyHash(hash){
    console.log(hash)
    navigator.clipboard.writeText(String(hash));
    const button = document.querySelector("#shareHash"+String(hash))
    button.innerHTML = 'Copied'
}