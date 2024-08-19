package org.fcitx.fcitx5.android.common.ipc;

interface IClipboardEntryTransformer {
   /** Transformers will be chained an applied to clipboard entry, where higher priority one goes first */
   int getPriority();
   /** The callback (all transformers are called sequentially in a separate thread on each clipboard update) */
   String transform(String clipboardText);
   /** Unique description of this transformer */
   String getDescription();
}