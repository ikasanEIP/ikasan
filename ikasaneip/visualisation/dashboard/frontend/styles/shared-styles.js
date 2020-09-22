import '@polymer/polymer/lib/elements/custom-style.js';
const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style>
    <style>
        html {
            --material-primary-color-10pct: rgba(241, 90, 35, 0.1);
            --material-primary-color-50pct: rgba(241, 90, 35, 0.5);
            --material-primary-color: rgba(241, 90, 35, 1.0);
            --material-primary-contrast-color: #FFF;
            --material-font-family: "Arial Bold", sans-serif;
        }

        vaadin-grid {

            --material-size-xs: 10px;
            --material-size-s: 12px;
            --material-size-m: 14px;
            --material-size-l: 16px;
            --material-size-xl: 20px;
            /* Font sizes (will affect spacing also) */
            --material-font-size-xxs: 9px;
            --material-font-size-xs: 10px;
            --material-font-size-s: 11px;
            --material-font-size-m: 12px;
            --material-font-size-l: 13px;
            --material-font-size-xl: 14px;
            --material-font-size-xxl: 15px;
            --material-font-size-xxxl: 19px;
            /* Icon size */
            --material-icon-size-s: 1em;
            --material-icon-size-m: 1.25em;
            --material-icon-size-l: 1.5em;
            /* Line height */
            --material-line-height-xs: 1.1;
            --material-line-height-s: 1.3;
            --material-line-height-m: 1.5;
        }

        vaadin-combo-box {

            --material-size-xs: 20px;
            --material-size-s: 24px;
            --material-size-m: 28px;
            --material-size-l: 32px;
            --material-size-xl: 40px;
            /* Font sizes (will affect spacing also) */
            --material-font-size-xxs: 10px;
            --material-font-size-xs: 11px;
            --material-font-size-s: 12px;
            --material-font-size-m: 14px;
            --material-font-size-l: 16px;
            --material-font-size-xl: 20px;
            --material-font-size-xxl: 24px;
            --material-font-size-xxxl: 32px;
            /* Icon size */
            --material-icon-size-s: 1em;
            --material-icon-size-m: 1.25em;
            --material-icon-size-l: 1.5em;
            /* Line height */
            --material-line-height-xs: 1.1;
            --material-line-height-s: 1.3;
            --material-line-height-m: 1.5;
        }

    </style>
</custom-style>

<dom-module theme-for="vaadin-grid" id="my-grid">
    <template>
        <style>
            [part~="row"]:hover [part~="body-cell"]{
                color: red;
                background-color: rgba(241, 90, 35, 0.1);
            }
        </style>
    </template>
</dom-module>

<dom-module theme-for="vaadin-text-field" id="ikasan-small">
    <template>
        <style>
            vaadin-text-field {

                --material-size-xs: 10px;
                --material-size-s: 12px;
                --material-size-m: 14px;
                --material-size-l: 16px;
                --material-size-xl: 20px;
                /* Font sizes (will affect spacing also) */
                --material-font-size-xxs: 7px;
                --material-font-size-xs: 8px;
                --material-font-size-s: 9px;
                --material-font-size-m: 10px;
                --material-font-size-l: 11px;
                --material-font-size-xl: 13px;
                --material-font-size-xxl: 15px;
                --material-font-size-xxxl: 19px;
                /* Icon size */
                --material-icon-size-s: 1em;
                --material-icon-size-m: 1.25em;
                --material-icon-size-l: 1.5em;
                /* Line height */
                --material-line-height-xs: 1.1;
                --material-line-height-s: 1.3;
                --material-line-height-m: 1.5;
            }

        </style>
    </template>
</dom-module>

<dom-module id="dialog-fix" theme-for="vaadin-dialog-overlay">
    <template>
        <style>
            [part~="overlay"]{
                max-width: none !important;
            }
        </style>
    </template>
</dom-module>

<dom-module id="my-grid-styles" theme-for="vaadin-grid">
    <template>
        <style>
            [part~="cell"] {
                font-size: 8pt;
            }
            
            /* Background needs a stronger selector to not be overridden */
            [part~="cell"].running {
                background-color: rgba(5,227,108, 0.3);
                /*background-clip: padding-box; !* this has been added *!*/
                /*border-radius: 5px;*/
                /*color: white;*/
                /*border: 5px solid white;*/
            }

            [part~="cell"].stoppedInError {
                background-color: rgba(255, 0, 0, 0.3);
                /*!*background-clip: padding-box; !* this has been added *!*!*/
                /*border-radius: 5px;*/
                /*color: rgba(255, 0, 0, 0.3);*/
                /*border: 5px solid rgba(255, 0, 0, 0.3);*/
            }

            [part~="cell"].recovering {
                background-color: rgba(253,185,19, 0.3);
                /*background-clip: padding-box; !* this has been added *!*/
                /*border-radius: 5px;*/
                /*color: white;*/
                /*border: 5px solid white;*/
            }

            [part~="cell"].paused {
                background-color: rgba(133,181,225, 0.3);
                /*background-clip: padding-box; !* this has been added *!*/
                /*border-radius: 5px;*/
                /*color: white;*/
                /*border: 5px solid white;*/
            }

            [part~="cell"].startPause {
                background-color: rgba(133,181,225, 0.3);
                /*background-clip: padding-box; !* this has been added *!*/
                /*border-radius: 5px;*/
                /*color: white;*/
                /*border: 5px solid white;*/
            }

            [part~="cell"].stopped {
                background-color: rgba(211,211,211, 0.3);
                /*background-clip: padding-box; !* this has been added *!*/
                /*border-radius: 5px;*/
                /*color: white;*/
                /*border: 5px solid white;*/
            }

        </style>


    </template>
</dom-module>`;

document.head.appendChild($_documentContainer.content);