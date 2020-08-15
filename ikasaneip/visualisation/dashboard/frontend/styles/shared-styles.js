import '@polymer/polymer/lib/elements/custom-style.js';
const $_documentContainer = document.createElement('template');

$_documentContainer.innerHTML = `<custom-style>
    <style>
        html {
            --lumo-primary-color-10pct: rgba(241, 90, 35, 0.1);
            --lumo-primary-color-50pct: rgba(241, 90, 35, 0.5);
            --lumo-primary-color: rgba(241, 90, 35, 1.0);
            --lumo-primary-contrast-color: #FFF;
            --lumo-font-family: "Arial Bold", sans-serif;
        }

        vaadin-grid {

            --lumo-size-xs: 10px;
            --lumo-size-s: 12px;
            --lumo-size-m: 14px;
            --lumo-size-l: 16px;
            --lumo-size-xl: 20px;
            /* Font sizes (will affect spacing also) */
            --lumo-font-size-xxs: 9px;
            --lumo-font-size-xs: 10px;
            --lumo-font-size-s: 11px;
            --lumo-font-size-m: 12px;
            --lumo-font-size-l: 13px;
            --lumo-font-size-xl: 14px;
            --lumo-font-size-xxl: 15px;
            --lumo-font-size-xxxl: 19px;
            /* Icon size */
            --lumo-icon-size-s: 1em;
            --lumo-icon-size-m: 1.25em;
            --lumo-icon-size-l: 1.5em;
            /* Line height */
            --lumo-line-height-xs: 1.1;
            --lumo-line-height-s: 1.3;
            --lumo-line-height-m: 1.5;
        }

        vaadin-combo-box {

            --lumo-size-xs: 20px;
            --lumo-size-s: 24px;
            --lumo-size-m: 28px;
            --lumo-size-l: 32px;
            --lumo-size-xl: 40px;
            /* Font sizes (will affect spacing also) */
            --lumo-font-size-xxs: 10px;
            --lumo-font-size-xs: 11px;
            --lumo-font-size-s: 12px;
            --lumo-font-size-m: 14px;
            --lumo-font-size-l: 16px;
            --lumo-font-size-xl: 20px;
            --lumo-font-size-xxl: 24px;
            --lumo-font-size-xxxl: 32px;
            /* Icon size */
            --lumo-icon-size-s: 1em;
            --lumo-icon-size-m: 1.25em;
            --lumo-icon-size-l: 1.5em;
            /* Line height */
            --lumo-line-height-xs: 1.1;
            --lumo-line-height-s: 1.3;
            --lumo-line-height-m: 1.5;
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

                --lumo-size-xs: 10px;
                --lumo-size-s: 12px;
                --lumo-size-m: 14px;
                --lumo-size-l: 16px;
                --lumo-size-xl: 20px;
                /* Font sizes (will affect spacing also) */
                --lumo-font-size-xxs: 7px;
                --lumo-font-size-xs: 8px;
                --lumo-font-size-s: 9px;
                --lumo-font-size-m: 10px;
                --lumo-font-size-l: 11px;
                --lumo-font-size-xl: 13px;
                --lumo-font-size-xxl: 15px;
                --lumo-font-size-xxxl: 19px;
                /* Icon size */
                --lumo-icon-size-s: 1em;
                --lumo-icon-size-m: 1.25em;
                --lumo-icon-size-l: 1.5em;
                /* Line height */
                --lumo-line-height-xs: 1.1;
                --lumo-line-height-s: 1.3;
                --lumo-line-height-m: 1.5;
            }

        </style>
    </template>
</dom-module>

<dom-module id="my-grid-styles" theme-for="vaadin-grid">
    <template>
        <style>
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