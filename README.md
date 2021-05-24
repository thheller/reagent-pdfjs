# reagent-pdfjs

Quick example showing how to use [pdf.js](https://mozilla.github.io/pdf.js/) with [reagent](https://github.com/reagent-project/reagent) in CLJS. This isn't specific to reagent so any React wrapper will work as long as function components are supported. This could be rewritten to not use hooks too.

I adapted the basic [Hello World Example](https://mozilla.github.io/pdf.js/examples/) directly from the `pdf.js` homepage.

I opted to use the CDN prebuilt distribution and not the `npm` package since that often seems to be several versions behind. It is also easier to deal with the "worker" pdf.js uses which is a pre-built `.js` file which would otherwise need to be copied manually from `node_modules/pdfjs-dist`.

```
git clone https://github.com/thheller/reagent-pdfjs.git
cd reagent-pdfjs
npm install
npx shadow-cljs watch demo
```

Wait for "Build completed." and open `http://localhost:8400` in your Browser. You should see a "Hello World" pdf displayed.

This example shows how to use React `useRef` in combination with `useEffect` to interop with foreign DOM libraries that don't use the React/reagent rendering model. It applies to many other libraries (eg. d3) in the same way. Create a DOM node, add a ref and use that via within the `useEffect` callback. Sometimes it may be necessary to "clean up" after yourself in that hook. Couldn't find anything about that in the pdf.js docs, so I only added a log where you'd do it.
