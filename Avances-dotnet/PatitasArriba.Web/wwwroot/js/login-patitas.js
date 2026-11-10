// Sala animada de la pantalla de inicio de sesión (Components/Pages/Seguridad/LoginPage.razor).
// Los perritos siguen el cursor, se emocionan al escribir el usuario y se tapan los ojos con la contraseña.
// LoginPage llama a iniciarSalaLogin al mostrarse, a celebrarLogin al ingresar y a detenerSalaLogin al salir.
(function () {
    let detener = null;
    let celebrar = null;

    window.iniciarSalaLogin = function () {
        if (detener) detener();
        if (!document.getElementById('sala')) return;
        let activo = true;

    /* ================= Piezas de los perritos (estilo plano, sin contorno) ================= */
    const INK = '#3a2e28';
    const CX = 120;                                                    // todos se dibujan centrados en x=120, con el piso en y=290

    // Ojo: blanco, pupila que se mueve, párpado del color del pelaje y una rayita que se ve con el ojo cerrado
    function ojo(id, lado, cx, cy, r, lid, iris) {
      const max = r * (iris ? .3 : .34);
      return `
      <g class="eye eye-${lado}">
        <clipPath id="${id}"><circle cx="${cx}" cy="${cy}" r="${r}"/></clipPath>
        <circle class="sclera" cx="${cx}" cy="${cy}" r="${r}" fill="#fff"/>
        <g clip-path="url(#${id})">
          <g class="pupil" data-max="${max}">
            ${iris ? `<circle cx="${cx}" cy="${cy}" r="${r * .68}" fill="${iris}"/>` : ''}
            <circle cx="${cx}" cy="${cy}" r="${r * (iris ? .36 : .6)}" fill="#231b17"/>
            <circle cx="${cx + r * .22}" cy="${cy - r * .24}" r="${r * .22}" fill="#fff"/>
            <circle cx="${cx - r * .2}" cy="${cy + r * .22}" r="${r * .09}" fill="#fff" opacity=".8"/>
          </g>
          <rect class="lid" x="${cx - r - 1}" y="${cy - r - 1}" width="${2 * r + 2}" height="${2 * r + 2}" fill="${lid}"/>
        </g>
        <path class="lash" d="M${cx - r * .75} ${cy + 1} Q${cx} ${cy + r * .6} ${cx + r * .75} ${cy + 1}" stroke="${INK}" stroke-width="3" fill="none" stroke-linecap="round"/>
      </g>`;
    }
    function collar(color, y, w, tag = '#f2b33d') {
      return `<path d="M${CX - w} ${y} Q${CX} ${y + 20} ${CX + w} ${y}" stroke="${color}" stroke-width="9" fill="none" stroke-linecap="round"/>
              <circle cx="${CX}" cy="${y + 17}" r="7" fill="${tag}"/><circle cx="${CX - 2}" cy="${y + 15}" r="2" fill="#fff" opacity=".7"/>`;
    }
    function nariz(y, w, color) {
      return `<path d="M${CX - w} ${y - 3} Q${CX} ${y - 10} ${CX + w} ${y - 3} Q${CX + w - 1} ${y + 7} ${CX} ${y + 9} Q${CX - w + 1} ${y + 7} ${CX - w} ${y - 3}Z" fill="${color}"/>
              <ellipse cx="${CX - w * .3}" cy="${y - 3}" rx="${w * .3}" ry="2" fill="#fff" opacity=".45"/>`;
    }
    // Boca cerrada (sonrisa) y boca abierta feliz con lengua (se ve al emocionarse)
    function boca(y, w, color) {
      return `
      <path class="mouth-closed" d="M${CX} ${y}v6M${CX} ${y + 6}q${-w * .55} ${w * .6} ${-w} 0M${CX} ${y + 6}q${w * .55} ${w * .6} ${w} 0" stroke="${color}" stroke-width="3.4" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
      <path class="mouth-open" d="M${CX - w} ${y + 4} Q${CX} ${y + w * 2.2 + 4} ${CX + w} ${y + 4} Q${CX} ${y + 9} ${CX - w} ${y + 4}Z" fill="#6b1d1d"/>
      <ellipse class="tongue-big" cx="${CX}" cy="${y + w * 1.35 + 4}" rx="${w * .5}" ry="${w * .55}" fill="#f47a92"/>`;
    }

    // Perro sentado de frente. Orden de capas: cola, muslos, cuerpo, pecho, patas, collar, cabeza.
    function perro(o) {
      const c = o.c;
      const h = Object.assign({ cy: 98, rx: 56, ry: 50 }, o.head);
      const e = Object.assign({ y: h.cy + 4, dx: 22, r: 13 }, o.eyes);
      const m = Object.assign({ y: h.cy + h.ry * .52, rx: 26, ry: 19, color: c.light }, o.muzzle);
      const neck = h.cy + 38, chin = h.cy + h.ry;
      const sw = o.sw || 38, hw = o.hw || 52, lw = o.legW || 22, lg = o.legGap || 4, lt = o.legTop || 200;
      const torso = `M${CX - sw} ${neck} C${CX - sw - 8} ${neck + 50} ${CX - hw} 226 ${CX - hw} 258 Q${CX - hw} 290 ${CX} 290 Q${CX + hw} 290 ${CX + hw} 258 C${CX + hw} 226 ${CX + sw + 8} ${neck + 50} ${CX + sw} ${neck}Z`;
      const cabeza = `M${CX - h.rx} ${h.cy} C${CX - h.rx} ${h.cy - h.ry * 1.25} ${CX + h.rx} ${h.cy - h.ry * 1.25} ${CX + h.rx} ${h.cy}
                      C${CX + h.rx} ${h.cy + h.ry * .75} ${CX + h.rx * .55} ${chin} ${CX} ${chin} C${CX - h.rx * .55} ${chin} ${CX - h.rx} ${h.cy + h.ry * .75} ${CX - h.rx} ${h.cy}Z`;
      const legX = [CX - lg - lw, CX + lg];
      const pata = x => `<path d="M${x} ${lt} H${x + lw} V277 Q${x + lw} 289 ${x + lw / 2} 289 Q${x} 289 ${x} 277Z" fill="${c.leg || c.fur}"/>`;
      const pie = x => `<ellipse cx="${x + lw / 2}" cy="286" rx="${lw / 2 + 5}" ry="7.5" fill="${c.paw}"/>
                        <path d="M${x + lw / 2 - 4} 282v5M${x + lw / 2 + 4} 282v5" stroke="${c.shade}" stroke-width="2.4" stroke-linecap="round"/>`;
      const ny = m.y - m.ry * .42;
      return `
    <g class="dog ${o.cls}" data-pw="${o.pw}"><g class="breathe">
      <defs><clipPath id="t-${o.cls}"><path d="${torso}"/></clipPath><clipPath id="h-${o.cls}"><path d="${cabeza}"/></clipPath></defs>
      <g class="tail"><path d="${o.tail}" stroke="${c.tail || c.fur}" stroke-width="${o.tailW || 10}" fill="none" stroke-linecap="round"/></g>
      <ellipse cx="${CX - hw + 2}" cy="254" rx="30" ry="33" fill="${c.shade}"/><ellipse cx="${CX + hw - 2}" cy="254" rx="30" ry="33" fill="${c.shade}"/>
      <ellipse cx="${CX - hw - 8}" cy="286" rx="15" ry="7" fill="${c.paw}"/><ellipse cx="${CX + hw + 8}" cy="286" rx="15" ry="7" fill="${c.paw}"/>
      <path d="${torso}" fill="${c.fur}"/>
      <g clip-path="url(#t-${o.cls})">
        <ellipse cx="${CX}" cy="${neck + 28}" rx="${sw * .62}" ry="${Math.min(44, lt - neck - 26)}" fill="${c.chest || c.light}"/>
        ${o.body || ''}
        <rect x="${legX[0] - 4}" y="${lt + 4}" width="${2 * (lg + lw) + 8}" height="${292 - lt}" rx="${lw / 2 + 4}" fill="${c.shade}"/>
      </g>
      ${pata(legX[0])}${pata(legX[1])}
      <path d="M${legX[1] + lw - 5} ${lt + 8} V280" stroke="${c.shade}" stroke-width="4" stroke-linecap="round" opacity=".55"/>
      ${pie(legX[0])}${pie(legX[1])}
      ${o.gear !== undefined ? o.gear : collar(c.collar, chin - 6, sw - 2)}
      <ellipse cx="${CX}" cy="${chin + 3}" rx="${h.rx * .55}" ry="8" fill="${c.shade}" opacity=".55"/>
      <g class="head-look"><g class="head-pose">
        <g class="ear ear-l">${o.ear}</g><g class="ear ear-r"><g transform="translate(${2 * CX} 0) scale(-1 1)">${o.ear}</g></g>
        ${o.behindHead || ''}
        <path d="${cabeza}" fill="${c.fur}"/>
        <g clip-path="url(#h-${o.cls})">
          ${o.marks || ''}
          <ellipse cx="${CX}" cy="${m.y}" rx="${m.rx}" ry="${m.ry}" fill="${m.color}"/>
          <ellipse cx="${CX - h.rx * .45}" cy="${h.cy - h.ry * .62}" rx="12" ry="5" fill="#fff" opacity=".28" transform="rotate(-20 ${CX - h.rx * .45} ${h.cy - h.ry * .62})"/>
        </g>
        ${o.preFace || ''}
        ${ojo(`${o.cls}-ol`, 'l', CX - e.dx, e.y, e.r, e.lid || c.fur, e.iris)}${ojo(`${o.cls}-or`, 'r', CX + e.dx, e.y, e.r, e.lid || c.fur, e.iris)}
        ${o.brows !== false ? [-1, 1].map(s => `<path class="brow" d="M${CX + s * e.dx - e.r * .8} ${e.y - e.r - 6} Q${CX + s * e.dx} ${e.y - e.r - 12} ${CX + s * e.dx + e.r * .8} ${e.y - e.r - 6}" stroke="${c.brow}" stroke-width="4" fill="none" stroke-linecap="round"/>`).join('') : ''}
        ${o.face || ''}
        ${boca(ny + 7, o.mouthW || 10, c.mouth || INK)}
        ${nariz(ny, o.noseW || 11, c.nose)}
        ${o.overFace || ''}
        <ellipse class="blush" cx="${CX - e.dx - 6}" cy="${e.y + e.r + 9}" rx="10" ry="6" fill="#f58aa8"/>
        <ellipse class="blush" cx="${CX + e.dx + 6}" cy="${e.y + e.r + 9}" rx="10" ry="6" fill="#f58aa8"/>
      </g></g>
    </g></g>`;
    }

    /* ================= Los 3 chihuahuas (tus perritos) ================= */
    // Chihuahua 1: crema, orejotas paradas rosadas, carita de sueño, collar celeste
    const chiA = () => perro({
      cls: 'chi1', pw: 'aside',
      c: { fur: '#f7eddd', shade: '#e4d1b3', light: '#fdf9f2', paw: '#fdf9f2', nose: '#4a3832', collar: '#38bdf8', brow: '#c9a57e' },
      head: { cy: 100, rx: 52, ry: 46 }, eyes: { dx: 21, r: 13, iris: '#7a4a2a' }, muzzle: { rx: 22, ry: 16 },
      sw: 28, hw: 42, legW: 16, legTop: 204, mouthW: 8, noseW: 9,
      ear: `<path d="M74 88 C46 74 26 48 18 14 C52 14 92 32 114 62Z" fill="#ebcda8"/><path d="M76 76 C54 64 40 46 34 26 C58 28 88 42 104 62Z" fill="#f4b6b0"/>`,
      tail: 'M156 272 C188 272 200 250 192 222', tailW: 7
    });
    // Chihuahua 2: crema regordita, orejas medio dobladas, ojazos café claro, collar rosado
    const chiB = () => perro({
      cls: 'chi2', pw: 'down',
      c: { fur: '#f5e4c4', shade: '#e1c89c', light: '#fdf4e4', paw: '#fdf4e4', nose: '#5b4b4b', collar: '#f472b6', brow: '#cfae7c' },
      head: { cy: 100, rx: 56, ry: 50 }, eyes: { dx: 23, r: 15, iris: '#8b5e34', lid: '#ecd3a6' }, muzzle: { rx: 25, ry: 18 },
      sw: 34, hw: 50, legW: 19, legTop: 202, noseW: 10,
      ear: `<path d="M78 68 C52 52 28 64 24 98 C44 106 68 94 88 80Z" fill="#dfc192"/><path d="M72 74 C54 68 40 78 38 94 C54 98 70 90 82 80Z" fill="#f4b6b0"/>`,
      marks: `<ellipse cx="${CX - 23}" cy="104" rx="22" ry="19" fill="#ecd3a6"/><ellipse cx="${CX + 23}" cy="104" rx="22" ry="19" fill="#ecd3a6"/>`,
      tail: 'M162 272 C194 270 206 248 198 220', tailW: 8
    });
    // Chihuahua 3: canela con frente y hocico blancos, orejotas redondas, OJOS VERDES, bigotes, collar amarillo
    const chiC = () => perro({
      cls: 'chi3', pw: 'down',
      c: { fur: '#e3c69a', shade: '#cba574', light: '#f8efe2', paw: '#f8efe2', nose: '#a8665f', collar: '#facc15', brow: '#a97c4a' },
      head: { cy: 100, rx: 55, ry: 48 }, eyes: { dx: 23, r: 14, iris: '#4ea35b', lid: '#c99a68' }, muzzle: { rx: 25, ry: 18 },
      sw: 30, hw: 44, legW: 17, legTop: 204, noseW: 10, mouthW: 9,
      ear: `<ellipse cx="58" cy="52" rx="31" ry="40" transform="rotate(-50 58 52)" fill="#d8b485"/><ellipse cx="60" cy="55" rx="18" ry="27" transform="rotate(-50 60 55)" fill="#f4b6b0"/>`,
      marks: `<ellipse cx="${CX - 23}" cy="104" rx="23" ry="20" fill="#c99a68"/><ellipse cx="${CX + 23}" cy="104" rx="23" ry="20" fill="#c99a68"/>
              <path d="M120 48 Q107 76 113 112 L127 112 Q133 76 120 48Z" fill="#f8efe2"/>`,
      overFace: `<g stroke="#fff" stroke-width="1.8" stroke-linecap="round" opacity=".95" fill="none"><path d="M100 132 q-22 -4 -38 3M100 138 q-22 3 -36 12M140 132 q22 -4 38 3M140 138 q22 3 36 12"/></g>`,
      tail: 'M156 272 C188 272 200 250 192 222', tailW: 7
    });

    /* ================= Los perritos nuevos ================= */
    // SCOTTISH TERRIER: negro azulado, cejas y barba grises, orejas en punta, pañuelo a cuadros
    const scottie = () => perro({
      cls: 'scottie', pw: 'aside',
      c: { fur: '#3a3e53', shade: '#2a2d3d', light: '#3f4459', chest: '#43485f', paw: '#4a4f68', nose: '#17100c', mouth: '#7d7584', brow: '#d6d0da' },
      head: { cy: 98, rx: 52, ry: 48 }, eyes: { dx: 21, r: 13 }, muzzle: { y: 128, rx: 30, ry: 22, color: '#cbc5cf' },
      sw: 40, hw: 54, legW: 22, legTop: 214, noseW: 13, mouthW: 9, brows: false,
      ear: `<path d="M86 66 L76 4 C96 16 112 34 116 56Z" fill="#2a2d3d"/><path d="M88 56 L82 20 C94 28 104 40 107 54Z" fill="#ee7d4f"/>`,
      behindHead: `<path d="M106 56 Q104 36 94 28 Q114 30 121 44 Q126 28 140 22 Q134 38 140 56Z" fill="#3a3e53"/>`,
      gear: `<path d="M80 140 Q120 164 160 140 L120 214Z" fill="url(#plaid)"/><circle cx="120" cy="156" r="5" fill="#fff7e6"/>`,
      preFace: `<path d="M88 118 Q120 108 152 118 Q166 140 158 162 L150 172 L142 162 L134 176 L126 164 L120 178 L114 164 L106 176 L98 162 L90 172 L82 162 Q74 140 88 118Z" fill="#cbc5cf"/>`,
      face: `<path class="brow" d="M80 86 Q98 72 116 84" stroke="#d6d0da" stroke-width="8" fill="none" stroke-linecap="round"/>
             <path class="brow" d="M160 86 Q142 72 124 84" stroke="#d6d0da" stroke-width="8" fill="none" stroke-linecap="round"/>`,
      body: `<path d="M60 278 l8 -14 l7 14 l8 -12 l7 12 l8 -14 l7 14 M140 278 l8 -14 l7 14 l8 -12 l7 12 l8 -14 l7 14" fill="#2a2d3d"/>`,
      tail: 'M164 262 C190 252 198 232 192 206', tailW: 11
    });

    // CORGI: naranja con pecho, antifaz y medias blancas, orejotas, collar azul (patitas cortas)
    const corgi = () => perro({
      cls: 'corgi', pw: 'down',
      c: { fur: '#ea8b3d', shade: '#cc6f27', light: '#fff5e6', leg: '#fff5e6', paw: '#fff5e6', nose: '#2a1d17', collar: '#3b82f6', brow: '#b5571a' },
      head: { cy: 100, rx: 57, ry: 48 }, eyes: { dx: 22, r: 12 }, muzzle: { rx: 27, ry: 19 },
      sw: 42, hw: 58, legW: 22, legGap: 5, legTop: 226,
      ear: `<path d="M80 74 L54 -4 C88 4 110 28 116 58Z" fill="#ea8b3d"/><path d="M82 62 L64 12 C86 20 102 36 108 56Z" fill="#f8cdb6"/>`,
      marks: `<path d="M120 46 Q109 70 113 98 Q84 104 76 138 Q98 156 120 156 Q142 156 164 138 Q156 104 127 98 Q131 70 120 46Z" fill="#fff5e6"/>`,
      body: `<ellipse cx="120" cy="190" rx="34" ry="50" fill="#fff5e6"/>`,
      tail: 'M168 262 C186 256 192 244 188 232', tailW: 18
    });

    // PUG: crema con antifaz oscuro, orejitas dobladas, frente arrugadita, lengüita siempre afuera, cola enrulada
    const pug = () => perro({
      cls: 'pug', pw: 'aside',
      c: { fur: '#efd8ad', shade: '#d6b986', light: '#f6e6c8', paw: '#f6e6c8', nose: '#151212', collar: '#ef4444', brow: '#cfb07c', mouth: '#e8d6b4' },
      head: { cy: 100, rx: 60, ry: 50 }, eyes: { dx: 25, r: 15, iris: '#5a3420', lid: '#4c413c' }, muzzle: { y: 132, rx: 34, ry: 25, color: '#4c413c' },
      sw: 42, hw: 56, legW: 22, legTop: 216, noseW: 12,
      ear: `<path d="M72 64 C48 54 32 70 34 94 C52 92 70 80 86 70Z" fill="#5c5450"/>`,
      marks: `<ellipse cx="${CX - 25}" cy="104" rx="25" ry="23" fill="#4c413c" opacity=".9"/><ellipse cx="${CX + 25}" cy="104" rx="25" ry="23" fill="#4c413c" opacity=".9"/>
              <path d="M94 62 q26 -10 52 0M102 74 q18 -6 36 0" stroke="#d2b47f" stroke-width="3.4" fill="none" stroke-linecap="round"/>`,
      overFace: `<g class="tongue-out"><path d="M112 136 Q108 162 120 164 Q132 162 128 136Z" fill="#f06f7b"/><path d="M120 141 V155" stroke="#c94a5a" stroke-width="2.4" stroke-linecap="round"/></g>`,
      tail: 'M164 262 c22 -6 30 -26 16 -36 c-12 -8 -22 4 -12 12', tailW: 9
    });

    // BOSTON TERRIER: solo se le ve la cabeza y las patitas asomando detrás del sillón
    function boston() {
      const fur = '#5f6472', blanco = '#f4efe4', cy = 100, rx = 58, ry = 52, chin = cy + ry;
      const cabeza = `M${CX - rx} ${cy} C${CX - rx} ${cy - ry * 1.25} ${CX + rx} ${cy - ry * 1.25} ${CX + rx} ${cy} C${CX + rx} ${cy + ry * .75} ${CX + rx * .55} ${chin} ${CX} ${chin} C${CX - rx * .55} ${chin} ${CX - rx} ${cy + ry * .75} ${CX - rx} ${cy}Z`;
      const oreja = `<path d="M78 76 L60 -6 C96 4 116 28 120 58Z" fill="${fur}"/><path d="M82 62 L70 12 C92 22 106 36 110 54Z" fill="#d9a5ab"/>`;
      return `
    <g class="dog boston" data-pw="hide"><g class="sink">
      <defs><clipPath id="h-boston"><path d="${cabeza}"/></clipPath></defs>
      <path d="M82 132 Q78 200 76 290 H164 Q162 200 158 132Z" fill="${fur}"/>
      <path d="M98 150 L120 270 L142 150Z" fill="${blanco}"/>
      ${collar('#ef4444', chin - 6, 36, '#e9b949')}
      <ellipse cx="${CX}" cy="${chin + 3}" rx="32" ry="8" fill="#454956" opacity=".6"/>
      <g class="head-look"><g class="head-pose">
        <g class="ear ear-l">${oreja}</g><g class="ear ear-r"><g transform="translate(${2 * CX} 0) scale(-1 1)">${oreja}</g></g>
        <path d="${cabeza}" fill="${fur}"/>
        <g clip-path="url(#h-boston)">
          <path d="M120 42 Q108 66 112 96 Q76 104 68 142 Q92 160 120 160 Q148 160 172 142 Q164 104 128 96 Q132 66 120 42Z" fill="${blanco}"/>
        </g>
        ${ojo('boston-ol', 'l', CX - 24, 102, 15, fur)}${ojo('boston-or', 'r', CX + 24, 102, 15, fur)}
        ${[-1, 1].map(s => `<path class="brow" d="M${CX + s * 24 - 12} 81 Q${CX + s * 24} 75 ${CX + s * 24 + 12} 81" stroke="#3d414c" stroke-width="4" fill="none" stroke-linecap="round"/>`).join('')}
        ${boca(134, 11, INK)}
        ${nariz(127, 13, '#1d1f26')}
        <ellipse class="blush" cx="88" cy="128" rx="10" ry="6" fill="#f58aa8"/><ellipse class="blush" cx="152" cy="128" rx="10" ry="6" fill="#f58aa8"/>
      </g></g>
    </g></g>`;
    }

    /* ================= La sala ================= */
    // Coloca un perro: (x, y) = punto del piso bajo sus patas en la sala, s = escala
    function ubicar(svg, x, y, s, sombra = 72, ay = 290) {
      return (sombra ? `<ellipse cx="${x}" cy="${y - 2}" rx="${sombra * s}" ry="${11 * s}" fill="#000" opacity=".12"/>` : '') +
        `<g transform="translate(${x - CX * s} ${y - ay * s}) scale(${s})">${svg}</g>`;
    }
    function hueso(x, y, w, color, rot = 0) {
      return `<g transform="translate(${x} ${y}) rotate(${rot})" fill="${color}">
        <rect x="${-w / 2}" y="-4" width="${w}" height="8" rx="3"/>
        <circle cx="${-w / 2}" cy="-5" r="6"/><circle cx="${-w / 2}" cy="5" r="6"/><circle cx="${w / 2}" cy="-5" r="6"/><circle cx="${w / 2}" cy="5" r="6"/></g>`;
    }

    const defs = `
    <defs>
      <linearGradient id="cielo" x1="0" y1="0" x2="0" y2="1"><stop offset="0" stop-color="#9fdcf8"/><stop offset="1" stop-color="#e6f7ff"/></linearGradient>
      <radialGradient id="luz"><stop offset="0" stop-color="#fff3c4" stop-opacity=".9"/><stop offset="1" stop-color="#fff3c4" stop-opacity="0"/></radialGradient>
      <clipPath id="vidrio"><rect x="450" y="112" width="300" height="250"/></clipPath>
      <g id="huella"><ellipse cx="0" cy="10" rx="14" ry="12"/><ellipse cx="-17" cy="-6" rx="6" ry="8"/><ellipse cx="-6" cy="-18" rx="6" ry="8"/><ellipse cx="7" cy="-18" rx="6" ry="8"/><ellipse cx="18" cy="-6" rx="6" ry="8"/></g>
      <pattern id="papel" width="120" height="120" patternUnits="userSpaceOnUse">
        <g fill="#3F6B52" opacity=".09">
          <use href="#huella" transform="translate(30 32) rotate(-15) scale(.55)"/>
          <use href="#huella" transform="translate(90 92) rotate(20) scale(.45)"/>
        </g>
      </pattern>
      <pattern id="plaid" width="18" height="18" patternUnits="userSpaceOnUse" patternTransform="rotate(45)">
        <rect width="18" height="18" fill="#f3a03b"/>
        <rect width="6" height="18" fill="#c8402f" opacity=".75"/>
        <rect width="18" height="6" fill="#c8402f" opacity=".55"/>
        <rect x="12" width="2" height="18" fill="#fde7b0" opacity=".85"/>
      </pattern>
    </defs>`;

    const pared = `
    <rect x="-1200" y="-1600" width="3600" height="2260" fill="#dce8d4"/>
    <rect x="-1200" y="-1600" width="3600" height="2260" fill="url(#papel)"/>
    <rect x="-1200" y="500" width="3600" height="140" fill="#4f7d62"/>
    <g fill="#456f57">
      ${[-1130, -900, -670, -440, -210, 20, 250, 480, 710, 940, 1170, 1400, 1630, 1860].map(x => `<rect x="${x}" y="520" width="210" height="100" rx="8"/><rect x="${x + 6}" y="526" width="198" height="88" rx="6" fill="#4f7d62"/>`).join('')}
    </g>
    <rect x="-1200" y="490" width="3600" height="14" fill="#3d654f"/>
    <rect x="-1200" y="628" width="3600" height="20" fill="#325642"/>
    ${[-160, 240, 640, 1040].map((x, k) => {                       // guirnalda de banderines
      const col = ['#B5483A', '#F2B33D', '#3F6B52', '#e88fa0', '#7fa88a'];
      let s = `<path d="M${x} 6 Q${x + 200} 64 ${x + 400} 6" stroke="#8a5a36" stroke-width="3" fill="none"/>`;
      for (let i = 1; i < 8; i++) {
        const t = i / 8, px = x + 400 * t, py = 6 + 2 * t * (1 - t) * 58;
        s += `<path d="M${px - 14} ${py} L${px + 14} ${py} L${px} ${py + 32}Z" fill="${col[(i + k * 2) % 5]}"/>`;
      }
      return s;
    }).join('')}`;

    const ventana = `
    <rect x="436" y="98" width="340" height="290" rx="12" fill="#000" opacity=".06"/>
    <rect x="430" y="92" width="340" height="290" rx="12" fill="#fffaf2"/>
    <rect x="450" y="112" width="300" height="250" fill="url(#cielo)"/>
    <g clip-path="url(#vidrio)">
      <circle cx="690" cy="168" r="70" fill="url(#luz)"/>
      <circle cx="690" cy="168" r="30" fill="#ffd56b"/>
      <g class="nube"><ellipse cx="520" cy="180" rx="40" ry="14" fill="#fff"/><ellipse cx="540" cy="168" rx="22" ry="16" fill="#fff"/><ellipse cx="510" cy="172" rx="16" ry="12" fill="#fff"/></g>
      <g class="nube n2"><ellipse cx="560" cy="250" rx="34" ry="11" fill="#fff" opacity=".9"/><ellipse cx="574" cy="241" rx="18" ry="12" fill="#fff" opacity=".9"/></g>
      <path d="M450 320 Q530 278 620 312 Q690 286 750 300 V362 H450Z" fill="#8fd18a"/>
      <path d="M450 340 Q560 318 650 336 Q710 324 750 334 V362 H450Z" fill="#6fbf72"/>
      <rect x="492" y="270" width="10" height="50" fill="#8b5a2b"/><circle cx="497" cy="262" r="26" fill="#4fae5c"/><circle cx="512" cy="276" r="18" fill="#5cc06a"/>
      <rect x="450" y="112" width="300" height="250" fill="none" stroke="#000" stroke-opacity=".07" stroke-width="10"/>
    </g>
    <path d="M600 112 V362M450 237 H750" stroke="#fffaf2" stroke-width="12"/>
    <rect x="414" y="376" width="372" height="22" rx="7" fill="#fffaf2"/>
    <rect x="420" y="396" width="360" height="6" rx="3" fill="#000" opacity=".08"/>
    <rect x="706" y="346" width="32" height="30" rx="5" fill="#e07a5f"/>
    <rect x="712" y="306" width="20" height="44" rx="10" fill="#5cb874"/>
    <path d="M712 326 h-8 q-4 0 -4 -6 v-8" fill="none" stroke="#5cb874" stroke-width="7" stroke-linecap="round"/>
    <circle cx="722" cy="304" r="4.5" fill="#f472b6"/>
    <line x1="378" y1="74" x2="822" y2="74" stroke="#8a5a36" stroke-width="7" stroke-linecap="round"/>
    <circle cx="374" cy="74" r="10" fill="#b07a4a"/><circle cx="826" cy="74" r="10" fill="#b07a4a"/>
    ${[0, 1].map(i => `<g ${i ? 'transform="translate(1200 0) scale(-1 1)"' : ''}>
      <path d="M392 72 H470 Q462 200 480 300 Q446 330 456 452 H398 Q382 300 392 72Z" fill="#5b8a6c"/>
      <path d="M420 86 Q414 220 422 440M446 86 Q442 200 454 300" stroke="#4b7a5c" stroke-width="6" fill="none" stroke-linecap="round"/>
      <path d="M396 300 Q440 316 482 298" stroke="#f2b84b" stroke-width="10" fill="none" stroke-linecap="round"/>
    </g>`).join('')}`;

    const cuadros = `
    <rect x="182" y="162" width="120" height="140" rx="6" fill="#000" opacity=".06"/>
    <rect x="176" y="156" width="120" height="140" rx="6" fill="#b07a4a"/>
    <rect x="190" y="170" width="92" height="112" fill="#fff7e6"/>
    <use href="#huella" transform="translate(236 226) scale(1.25)" fill="#3F6B52"/>
    <circle cx="344" cy="226" r="40" fill="#f2b84b"/>
    <circle cx="344" cy="226" r="30" fill="#fff7e6"/>
    <path d="M344 244 C322 230 324 210 336 210 C342 210 344 216 344 218 C344 216 346 210 352 210 C364 210 366 230 344 244Z" fill="#f472b6"/>
    <circle cx="969" cy="172" r="50" fill="#000" opacity=".06"/>
    <circle cx="965" cy="168" r="50" fill="#b07a4a"/>
    <circle cx="965" cy="168" r="40" fill="#fffaf0"/>
    <g stroke="${INK}" stroke-width="3" stroke-linecap="round">
      ${[0, 90, 180, 270].map(a => `<line x1="965" y1="132" x2="965" y2="140" transform="rotate(${a} 965 168)"/>`).join('')}
    </g>
    <line id="aguja-h" x1="965" y1="168" x2="965" y2="146" stroke="${INK}" stroke-width="5" stroke-linecap="round"/>
    <line id="aguja-m" x1="965" y1="168" x2="965" y2="136" stroke="${INK}" stroke-width="3.5" stroke-linecap="round"/>
    <line id="aguja-s" x1="965" y1="174" x2="965" y2="134" stroke="#e0607e" stroke-width="2" stroke-linecap="round"/>
    <circle cx="965" cy="168" r="4" fill="${INK}"/>
    <rect x="858" y="318" width="224" height="14" rx="4" fill="#b07a4a"/>
    <path d="M890 332 v18 h14M1050 332 v18 h-14" stroke="#8a5a36" stroke-width="5" fill="none"/>
    <rect x="876" y="264" width="18" height="54" rx="3" fill="#e07a5f"/>
    <rect x="896" y="272" width="16" height="46" rx="3" fill="#3F6B52"/>
    <rect x="914" y="276" width="18" height="44" rx="3" fill="#f2b84b" transform="rotate(12 923 318)"/>
    <rect x="958" y="276" width="50" height="42" rx="10" fill="#e6f6f9"/>
    <rect x="954" y="266" width="58" height="12" rx="5" fill="#e07a5f"/>
    ${hueso(983, 300, 20, '#f3d9a8')}
    <path d="M1030 318 h34 l-4 -26 h-26Z" fill="#c8664c"/>
    <path d="M1040 294 q-14 30 -6 70M1054 294 q12 40 4 80M1047 294 q0 30 8 46" stroke="#2f8f57" stroke-width="5" fill="none" stroke-linecap="round"/>
    <g fill="#4fb86a">
      <ellipse cx="1036" cy="330" rx="7" ry="4" transform="rotate(-50 1036 330)"/><ellipse cx="1034" cy="356" rx="7" ry="4" transform="rotate(40 1034 356)"/>
      <ellipse cx="1060" cy="340" rx="7" ry="4" transform="rotate(50 1060 340)"/><ellipse cx="1058" cy="370" rx="7" ry="4" transform="rotate(-40 1058 370)"/>
      <ellipse cx="1052" cy="282" rx="10" ry="6" transform="rotate(-30 1052 282)"/><ellipse cx="1036" cy="284" rx="10" ry="6" transform="rotate(30 1036 284)"/>
    </g>`;

    const piso = `
    <rect x="-1200" y="646" width="3600" height="354" fill="#e9bb85"/>
    <g stroke="#dcaa72" stroke-width="3" fill="none">
      <path d="M-1200 682 H2400M-1200 726 H2400M-1200 778 H2400M-1200 840 H2400M-1200 912 H2400M-1200 992 H2400"/>
      ${[-1200, 0, 1200].map(dx => `<path transform="translate(${dx} 0)" d="M140 646V682M520 646V682M900 646V682M60 682V726M330 682V726M760 682V726M1100 682V726M220 726V778M610 726V778M980 726V778M90 778V840M450 778V840M820 778V840M300 840V912M700 840V912M1080 840V912M160 912V992M560 912V992M940 912V992"/>`).join('')}
    </g>
    <ellipse cx="610" cy="910" rx="470" ry="84" fill="#7fa37a"/>
    <ellipse cx="610" cy="904" rx="462" ry="78" fill="#94b58d"/>
    <ellipse cx="610" cy="904" rx="428" ry="64" fill="none" stroke="#f3f8ef" stroke-width="4" stroke-dasharray="16 10"/>
    <ellipse cx="610" cy="904" rx="356" ry="48" fill="#a9c6a1"/>
    <ellipse cx="610" cy="904" rx="250" ry="32" fill="none" stroke="#f3f8ef" stroke-width="3" opacity=".7"/>`;

    // Planta y lámpara de pie a los costados (decoran en pantallas anchas)
    const costados = `
    <path d="M100 620 q-60 -60 -50 -150 q30 50 50 150Z" fill="#3e9d63"/>
    <path d="M100 620 q50 -90 20 -170 q-40 60 -20 170Z" fill="#4fb86a"/>
    <path d="M100 620 q-20 -100 30 -120 q10 60 -30 120Z" fill="#2f8f57"/>
    <path d="M66 616 h68 l-8 74 h-52Z" fill="#c8664c"/>
    <rect x="60" y="608" width="80" height="16" rx="5" fill="#e07a5f"/>
    <circle cx="1125" cy="370" r="90" fill="url(#luz)"/>
    <line x1="1125" y1="400" x2="1125" y2="690" stroke="#8a5a36" stroke-width="7"/>
    <ellipse cx="1125" cy="692" rx="34" ry="9" fill="#8a5a36"/>
    <path d="M1084 404 L1166 404 L1148 336 L1102 336Z" fill="#fff1d2"/>
    <path d="M1084 404 L1166 404 L1162 390 L1088 390Z" fill="#f2dfb6"/>`;

    const sillon = `
    <ellipse cx="946" cy="700" rx="140" ry="12" fill="#000" opacity=".12"/>
    <rect x="836" y="676" width="16" height="26" rx="4" fill="#8a5a36"/><rect x="1040" y="676" width="16" height="26" rx="4" fill="#8a5a36"/>
    <path d="M842 610 V522 Q842 482 882 482 H1010 Q1050 482 1050 522 V610Z" fill="#d99a2b"/>
    <rect x="862" y="500" width="168" height="112" rx="24" fill="#f2b84b"/>
    <rect x="830" y="604" width="232" height="80" rx="16" fill="#d99a2b"/>
    <rect x="856" y="590" width="180" height="40" rx="14" fill="#f7cb6e"/>
    <rect x="816" y="556" width="64" height="134" rx="28" fill="#f2b84b"/>
    <rect x="1012" y="556" width="64" height="134" rx="28" fill="#f2b84b"/>
    <rect x="826" y="562" width="44" height="10" rx="5" fill="#fff" opacity=".22"/><rect x="1022" y="562" width="44" height="10" rx="5" fill="#fff" opacity=".22"/>`;

    const sofa = `
    <ellipse cx="460" cy="698" rx="320" ry="14" fill="#000" opacity=".12"/>
    <rect x="196" y="672" width="16" height="26" rx="4" fill="#8a5a36"/><rect x="708" y="672" width="16" height="26" rx="4" fill="#8a5a36"/>
    <path d="M200 600 V502 Q200 468 236 468 H684 Q720 468 720 502 V600Z" fill="#B5483A"/>
    <rect x="222" y="488" width="232" height="112" rx="26" fill="#e07a5f"/>
    <rect x="466" y="488" width="232" height="112" rx="26" fill="#e07a5f"/>
    <rect x="186" y="600" width="548" height="80" rx="16" fill="#B5483A"/>
    <rect x="210" y="582" width="252" height="46" rx="16" fill="#ea8d73"/>
    <rect x="458" y="582" width="252" height="46" rx="16" fill="#ea8d73"/>
    <rect x="150" y="526" width="76" height="162" rx="34" fill="#e07a5f"/>
    <rect x="694" y="526" width="76" height="162" rx="34" fill="#e07a5f"/>
    <rect x="160" y="532" width="56" height="10" rx="5" fill="#fff" opacity=".2"/><rect x="704" y="532" width="56" height="10" rx="5" fill="#fff" opacity=".2"/>`;

    const cama = {
      atras: `<ellipse cx="935" cy="912" rx="138" ry="50" fill="#3F6B52"/>
              <ellipse cx="935" cy="898" rx="112" ry="30" fill="#f6ead2"/>`,
      adelante: `<path d="M797 906 Q802 962 935 962 Q1068 962 1073 906 Q1058 930 935 932 Q812 930 797 906Z" fill="#4f7d62"/>`
    };
    const juguetes = `${hueso(470, 968, 40, '#fff4dc', -14)}
    <circle cx="770" cy="970" r="17" fill="#ef4444"/>
    <path d="M756 962 Q770 972 784 960" stroke="#fff" stroke-width="4" fill="none" stroke-linecap="round"/>`;

    const bostonPatas = `<g class="boston-paws">${[918, 972].map(x => `<ellipse cx="${x}" cy="487" rx="18" ry="10" fill="#f4efe4"/>
      <path d="M${x - 5} 486v6M${x + 5} 486v6" stroke="#c9c2b4" stroke-width="2.4" stroke-linecap="round"/>`).join('')}</g>`;

    const sala = document.getElementById('sala');
    sala.innerHTML = defs + pared + ventana + cuadros + piso + costados +
      `<g transform="translate(${945 - CX * .8} ${452 - 100 * .8}) scale(.8)">${boston()}</g>` +   // detrás del respaldo del sillón
      sillon + bostonPatas + sofa +
      ubicar(chiA(), 465, 478, .62, 50) +                            // sentada arriba del respaldo, frente a la ventana
      ubicar(chiB(), 318, 620, .76) +                                // en el sofá
      ubicar(scottie(), 610, 620, .8) +                              // en el sofá
      ubicar(corgi(), 320, 942, .96) +                               // en la alfombra
      ubicar(chiC(), 640, 946, 1) +                                  // al frente, en la alfombra
      cama.atras + ubicar(pug(), 935, 930, .9, 0) + cama.adelante +  // en su camita
      juguetes;

    // Reloj de pared con la hora real
    function reloj() {
      const d = new Date(), h = d.getHours() % 12, m = d.getMinutes(), s = d.getSeconds();
      const gira = (id, a) => document.getElementById(id).setAttribute('transform', `rotate(${a} 965 168)`);
      gira('aguja-h', (h + m / 60) * 30); gira('aguja-m', (m + s / 60) * 6); gira('aguja-s', s * 6);
    }
    reloj(); const idReloj = setInterval(reloj, 1000);

    // Encuadre: siempre entra la zona de los perros (x 140–1060) apoyada en el piso; si sobra espacio se ve más pared o más sala
    function encuadrar() {
      const r = sala.clientWidth / sala.clientHeight;
      if (!r || !isFinite(r)) return;                                 // panel aún sin tamaño
      const w = Math.max(920, 1000 * r), h = w / r;
      sala.setAttribute('viewBox', `${600 - w / 2} ${1000 - h} ${w} ${h}`);
    }
    encuadrar();
    const observador = new ResizeObserver(encuadrar);
    observador.observe(sala);

    /* ================= Comportamiento ================= */
    const stage = document.getElementById('stage');
    const correo = document.getElementById('correo');
    const clave = document.getElementById('clave');
    const ver = document.getElementById('ver');
    const dogs = [...document.querySelectorAll('.dog')];
    const perros = dogs.map(d => ({
      el: d, pw: d.dataset.pw,
      head: d.querySelector('.head-look'),
      ojos: [...d.querySelectorAll('.eye')].map(e => {
        const p = e.querySelector('.pupil');
        return { s: e.querySelector('.sclera'), p, max: parseFloat(p.dataset.max) };
      })
    }));
    let mouse = { x: innerWidth * .6, y: innerHeight * .4 };
    let heartTimer = null, settleTimer = null;
    let miraCampo = true;                                              // en el correo: true = mira el campo, false = sigue el mouse

    function suave() {                                                // transición suave solo al cambiar de estado
      stage.classList.add('settling');
      clearTimeout(settleTimer);
      settleTimer = setTimeout(() => stage.classList.remove('settling'), 450);
    }
    function estado(s) {
      if (stage.dataset.state === s) return;
      stage.dataset.state = s;
      miraCampo = true;
      suave();
      clearInterval(heartTimer);
      if (s === 'email') heartTimer = setInterval(corazon, 420);
    }
    function corazon() {
      const d = dogs[Math.floor(Math.random() * dogs.length)].getBoundingClientRect();
      const c = stage.getBoundingClientRect();
      const h = document.createElement('span');
      h.className = 'heart'; h.textContent = '♥';
      h.style.left = (d.left - c.left + d.width * (.3 + Math.random() * .4)) + 'px';
      h.style.top = (d.top - c.top + d.height * .12) + 'px';
      h.style.setProperty('--dx', (Math.random() * 40 - 20) + 'px');
      stage.appendChild(h);
      setTimeout(() => h.remove(), 2000);
    }

    // Detalles de vida: parpadeos y orejas que se sacuden, cada perro a su ritmo
    function aveces(clase, dur, min, max) {
      (function tick() {
        setTimeout(() => {
          if (!activo) return;
          const p = perros[Math.floor(Math.random() * perros.length)].el;
          if (stage.dataset.state !== 'password') {
            p.classList.add(clase);
            setTimeout(() => p.classList.remove(clase), dur);
          }
          tick();
        }, min + Math.random() * (max - min));
      })();
    }
    aveces('blink', 150, 700, 1600);
    aveces('twitch', 550, 1500, 3200);

    // Punto al que miran: el campo mientras escribes, si no el mouse
    function enCampo(input) {
      const r = input.getBoundingClientRect();
      return { x: r.left + Math.min(input.value.length * 9 + 50, r.width - 14), y: r.top + r.height / 2 };
    }
    function objetivo(st) {
      if (st === 'peek') return enCampo(clave);                       // con la contraseña visible espían el campo
      if (st === 'email' && miraCampo) return enCampo(correo);
      return mouse;
    }
    // Vector desde el centro de un rectángulo hacia t, de largo máximo `max` (se acorta si t está muy cerca)
    function hacia(t, r, max, alcance) {
      const vx = t.x - (r.left + r.width / 2), vy = t.y - (r.top + r.height / 2), dist = Math.hypot(vx, vy) || 1, f = Math.min(1, dist / alcance);
      return [vx / dist * max * f, vy / dist * max * f];
    }

    // Bucle de animación: cabeza y pupilas siguen el objetivo cuadro a cuadro (sin retraso)
    function frame() {
      if (!activo) return;
      const st = stage.dataset.state, t = objetivo(st);
      const mira = st !== 'password';
      const rects = perros.map(p => p.ojos.map(o => o.s.getBoundingClientRect()));   // primero se lee todo…
      perros.forEach((p, i) => {                                                    // …y luego se escribe (un solo cálculo de layout por cuadro)
        const [a, b] = rects[i];
        let h = [0, 0];
        if (mira) {
          const [x, y] = hacia(t, { left: a.left, top: a.top, width: b.right - a.left, height: b.bottom - a.top }, 1, 220);
          h = [x * 9, y * 6];
        } else if (p.pw === 'aside') h = [-5, -3];
        p.head.style.transform = `translate(${h[0]}px, ${h[1]}px) rotate(${h[0] * .7}deg)`;
        p.ojos.forEach((o, j) => {
          const d = mira ? hacia(t, rects[i][j], o.max, 90) : [-o.max * .7, -o.max * .7];
          o.p.style.transform = `translate(${d[0]}px, ${d[1]}px)`;
        });
      });
      requestAnimationFrame(frame);
    }
    const alMover = e => { mouse = { x: e.clientX, y: e.clientY }; miraCampo = false; };
    addEventListener('mousemove', alMover);
    const alTocar = e => { const t = e.touches[0]; mouse = { x: t.clientX, y: t.clientY }; miraCampo = false; };
    addEventListener('touchmove', alTocar, { passive: true });
    requestAnimationFrame(frame);

    correo.addEventListener('focus', () => estado('email'));
    correo.addEventListener('input', () => { miraCampo = true; });   // al teclear vuelven a mirar el campo
    correo.addEventListener('blur',  () => { if (stage.dataset.state === 'email') estado('idle'); });
    clave.addEventListener('focus', () => estado(clave.type === 'text' ? 'peek' : 'password'));
    clave.addEventListener('blur',  () => estado('idle'));
    ver.addEventListener('mousedown', e => e.preventDefault());       // mantiene el foco en la contraseña
    ver.addEventListener('click', () => {
      const visible = clave.type === 'password';
      clave.type = visible ? 'text' : 'password';
      estado(visible ? 'peek' : (document.activeElement === clave ? 'password' : 'idle'));
    });
    // Al ingresar: saltan y salen corazones (Blazor llama a celebrarLogin antes de navegar)
    celebrar = () => {
      estado('idle');
      stage.classList.add('celebrate');
      for (let i = 0; i < 6; i++) setTimeout(corazon, i * 90);
    };

    detener = () => {
      activo = false;
      clearInterval(idReloj);
      clearInterval(heartTimer);
      clearTimeout(settleTimer);
      observador.disconnect();
      removeEventListener('mousemove', alMover);
      removeEventListener('touchmove', alTocar);
    };
    };

    window.celebrarLogin = function () {
        if (celebrar) celebrar();
    };

    window.detenerSalaLogin = function () {
        if (detener) detener();
        detener = null;
        celebrar = null;
    };
})();
